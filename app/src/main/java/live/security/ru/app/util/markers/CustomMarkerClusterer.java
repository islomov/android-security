package live.security.ru.app.util.markers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import ru.security.live.util.LoggingTool;
/**
 * @author sardor
 */
public abstract class CustomMarkerClusterer extends Overlay {

  /**
   * impossible value for zoom level, to force clustering
   */
  protected static final int FORCE_CLUSTERING = -1;

  protected ArrayList<Marker> mItems = new ArrayList<>();
  protected Point mPoint = new Point();
  protected ArrayList<CustomStaticCluster> mClusters = new ArrayList<>();
  protected int mLastZoomLevel;
  protected Bitmap mClusterIcon;
  protected String mName, mDescription;
  protected Marker.OnMarkerClickListener markerClickListener;
  // abstract methods:

  /**
   * clustering algorithm
   */
  public abstract ArrayList<CustomStaticCluster> clusterer(MapView mapView);

  /**
   * Build the marker for a cluster.
   */
  public abstract Marker buildClusterMarker(CustomStaticCluster cluster, MapView mapView);

  /**
   * build clusters markers to be used at next draw
   */
  public abstract void renderer(ArrayList<CustomStaticCluster> clusters, Canvas canvas, MapView mapView);

  public CustomMarkerClusterer() {
    super();
    mLastZoomLevel = FORCE_CLUSTERING;
  }

  public void setName(String name) {
    mName = name;
  }

  public String getName() {
    return mName;
  }

  public void setDescription(String description) {
    mDescription = description;
  }

  public String getDescription() {
    return mDescription;
  }

  /**
   * Set the cluster icon to be drawn when a cluster contains more than 1 marker.
   * If not set, default will be the default osmdroid marker icon (which is really inappropriate as a cluster icon).
   */
  public void setIcon(Bitmap icon) {
    mClusterIcon = icon;
  }

  /**
   * Add the Marker.
   * Important: Markers added in a MarkerClusterer should not be added in the map overlays.
   */
  public void add(Marker marker) {
    mItems.add(marker);
  }

  /**
   * Force a rebuild of clusters at next draw, even without a zooming action.
   * Should be done when you changed the content of a MarkerClusterer.
   */
  public void invalidate() {
    mLastZoomLevel = FORCE_CLUSTERING;
  }

  /**
   * @return the Marker at id (starting at 0)
   */
  public Marker getItem(int id) {
    return mItems.get(id);
  }

  /**
   * @return the list of Markers.
   */
  public ArrayList<Marker> getItems() {
    return mItems;
  }

  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    //if zoom has changed and mapView is now stable, rebuild clusters:
    int zoomLevel = mapView.getZoomLevel();
    if (zoomLevel != mLastZoomLevel && !mapView.isAnimating()) {
      mClusters = clusterer(mapView);
      renderer(mClusters, canvas, mapView);
      mLastZoomLevel = zoomLevel;
    }

    for (CustomStaticCluster cluster : mClusters) {
      cluster.getMarker().draw(canvas, mapView, shadow);
    }
  }

  public Iterable<CustomStaticCluster> reversedClusters() {
    return new Iterable<CustomStaticCluster>() {
      @Override
      public Iterator<CustomStaticCluster> iterator() {
        final ListIterator<CustomStaticCluster> i = mClusters.listIterator(mClusters.size());
        return new Iterator<CustomStaticCluster>() {
          @Override
          public boolean hasNext() {
            return i.hasPrevious();
          }

          @Override
          public CustomStaticCluster next() {
            return i.previous();
          }

          @Override
          public void remove() {
            i.remove();
          }
        };
      }
    };
  }

  @Override
  public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {
    boolean markerTap = false;
    CustomStaticCluster myCluster = null;
    for (final CustomStaticCluster cluster : reversedClusters()) {
      LoggingTool.Companion.log("Cluster size " + cluster.getSize());
      if (cluster.getMarker().onSingleTapConfirmed(event, mapView)) {
        LoggingTool.Companion.log("Tap on marker, id " + cluster.getMarker().getId());
        markerTap = cluster.getMarker().getId() != null;
        myCluster = cluster;
        LoggingTool.Companion.log("markerTap:" + markerTap);
        if (markerTap) break;
      }
    }
    LoggingTool.Companion.log("markerTap:" + markerTap + " myCluster:" + myCluster);
    if (!markerTap && myCluster != null) {
      LoggingTool.Companion.log("BoundingBox:" + markerTap);
      double maxLat = MarkerCoordinatesHelper.Companion.getMaxLat(myCluster.mItems);
      double maxLon = MarkerCoordinatesHelper.Companion.getMaxLon(myCluster.mItems);
      double minLat = MarkerCoordinatesHelper.Companion.getMinLat(myCluster.mItems);
      double minLon = MarkerCoordinatesHelper.Companion.getMinLon(myCluster.mItems);
      BoundingBox boundingBox = new BoundingBox(
          maxLat,
          maxLon,
          minLat,
          minLon
      );

      if (maxLat != minLat && minLon != maxLon) {
        mapView.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);
        //mapView.getController().setCenter();
      }


      //MapZoomHelper.zoomToBoundingBox(mapView, boundingBox, true);

      if (maxLat == minLat && minLon == maxLon) {
        markerClickListener.onMarkerClick(myCluster.mItems.get(0), null);
      }

    }
    return markerTap;
  }

  @Override
  public boolean onLongPress(final MotionEvent event, final MapView mapView) {
    for (final CustomStaticCluster cluster : reversedClusters()) {
      if (cluster.getMarker().onLongPress(event, mapView))
        return true;
    }
    return false;
  }

  @Override
  public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
    for (CustomStaticCluster cluster : reversedClusters()) {
      if (cluster.getMarker().onTouchEvent(event, mapView))
        return true;
    }
    return false;
  }

  void onMarkerBreak(boolean isBroken, Marker marker, MapView mapView) {
    if (isBroken) {
      mapView.getController().setCenter(marker.getPosition());
    } else {
      mapView.getController().zoomIn();
    }
  }

  public abstract void setOnClusterClickListener(Marker.OnMarkerClickListener listener);
}


