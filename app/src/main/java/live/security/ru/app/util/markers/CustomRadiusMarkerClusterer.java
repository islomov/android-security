package live.security.ru.app.util.markers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * @author sardor
 */
public class CustomRadiusMarkerClusterer extends CustomMarkerClusterer {

  protected int mMaxClusteringZoomLevel = 17;
  protected int mRadiusInPixels = 100;
  protected double mRadiusInMeters;
  protected Paint mTextPaint;
  private ArrayList<Marker> mClonedMarkers;
  /**
   * cluster icon anchor
   */
  public float mAnchorU = Marker.ANCHOR_CENTER, mAnchorV = Marker.ANCHOR_CENTER;
  /**
   * anchor point to draw the number of markers inside the cluster icon
   */
  public float mTextAnchorU = Marker.ANCHOR_CENTER, mTextAnchorV = Marker.ANCHOR_CENTER;

  public CustomRadiusMarkerClusterer(Context ctx) {
    super();
    mTextPaint = new Paint();
    mTextPaint.setColor(Color.WHITE);
    mTextPaint.setTextSize(15 * ctx.getResources().getDisplayMetrics().density);
    mTextPaint.setFakeBoldText(true);
    mTextPaint.setTextAlign(Paint.Align.CENTER);
    mTextPaint.setAntiAlias(true);
    Drawable clusterIconD = ctx.getResources().getDrawable(org.osmdroid.bonuspack.R.drawable.marker_cluster);
    Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
    setIcon(clusterIcon);

  }

  @Override
  public void setOnClusterClickListener(Marker.OnMarkerClickListener listener) {
    markerClickListener = listener;
  }

  /**
   * If you want to change the default text paint (color, size, font)
   */
  public Paint getTextPaint() {
    return mTextPaint;
  }

  /**
   * Set the radius of clustering in pixels. Default is 100px.
   */
  public void setRadius(int radius) {
    mRadiusInPixels = radius;
  }

  /**
   * Set max zoom level with clustering. When zoom is higher or equal to this level, clustering is disabled.
   * You can put a high value to disable this feature.
   */
  public void setMaxClusteringZoomLevel(int zoom) {
    mMaxClusteringZoomLevel = zoom;
  }

  /**
   * Radius-Based clustering algorithm
   */
  @Override
  public ArrayList<CustomStaticCluster> clusterer(MapView mapView) {

    ArrayList<CustomStaticCluster> clusters = new ArrayList<CustomStaticCluster>();
    convertRadiusToMeters(mapView);

    mClonedMarkers = new ArrayList<>(mItems); //shallow copy
    while (!mClonedMarkers.isEmpty()) {
      Marker m = mClonedMarkers.get(0);
      CustomStaticCluster cluster = createCluster(m, mapView);
      clusters.add(cluster);
    }
    return clusters;
  }

  private CustomStaticCluster createCluster(Marker m, MapView mapView) {
    GeoPoint clusterPosition = m.getPosition();

    CustomStaticCluster cluster = new CustomStaticCluster(clusterPosition);
    cluster.add(m);

    mClonedMarkers.remove(m);

    if (mapView.getZoomLevel() > mMaxClusteringZoomLevel) {
      //above max level => block clustering:
      return cluster;
    }

    Iterator<Marker> it = mClonedMarkers.iterator();
    while (it.hasNext()) {
      Marker neighbour = it.next();
      double distance = clusterPosition.distanceToAsDouble(neighbour.getPosition());
      if (distance <= mRadiusInMeters) {
        cluster.add(neighbour);
        it.remove();
      }
    }

    return cluster;
  }

  @Override
  public Marker buildClusterMarker(CustomStaticCluster cluster, MapView mapView) {
    Marker m = new Marker(mapView);
    m.setPosition(cluster.getPosition());
    m.setInfoWindow(null);
    m.setAnchor(mAnchorU, mAnchorV);

    Bitmap finalIcon = Bitmap.createBitmap(mClusterIcon.getWidth(), mClusterIcon.getHeight(), mClusterIcon.getConfig());
    Canvas iconCanvas = new Canvas(finalIcon);
    iconCanvas.drawBitmap(mClusterIcon, 0, 0, null);
    String text = "" + cluster.getSize();

    int textHeight = (int) (mTextPaint.descent() + mTextPaint.ascent());
    iconCanvas.drawText(text,
        mTextAnchorU * finalIcon.getWidth(),
        mTextAnchorV * finalIcon.getHeight() - textHeight / 2,
        mTextPaint);
    m.setIcon(new BitmapDrawable(mapView.getContext().getResources(), finalIcon));

    return m;
  }

  @Override
  public void renderer(ArrayList<CustomStaticCluster> clusters, Canvas canvas, MapView mapView) {
    boolean isBroken = false;
    Marker marker = null;
    for (CustomStaticCluster cluster : clusters) {
      if (cluster.getSize() == 1) {
        //cluster has only 1 marker => use it as it is:
        cluster.setMarker(cluster.getItem(0));
        isBroken = true;
        marker = cluster.getMarker();
      } else {
        //only draw 1 Marker at Cluster center, displaying number of Markers contained
        Marker m = buildClusterMarker(cluster, mapView);
        cluster.setMarker(m);
      }
    }
  }

  private void convertRadiusToMeters(MapView mapView) {

    Rect mScreenRect = mapView.getIntrinsicScreenRect(null);

    int screenWidth = mScreenRect.right - mScreenRect.left;
    int screenHeight = mScreenRect.bottom - mScreenRect.top;

    BoundingBox bb = mapView.getBoundingBox();

    double diagonalInMeters = bb.getDiagonalLengthInMeters();
    double diagonalInPixels = Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight);
    double metersInPixel = diagonalInMeters / diagonalInPixels;

    mRadiusInMeters = mRadiusInPixels * metersInPixel;
  }

}
