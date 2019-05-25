package live.security.ru.app.util.markers;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
/**
 * @author sardor
 */
public class CustomStaticCluster {
  public final ArrayList<Marker> mItems = new ArrayList<Marker>();
  protected GeoPoint mCenter;
  protected Marker mMarker;

  public CustomStaticCluster(GeoPoint center) {
    mCenter = center;
  }

  public void setPosition(GeoPoint center) {
    mCenter = center;
  }

  public GeoPoint getPosition() {
    return mCenter;
  }

  public int getSize() {
    return mItems.size();
  }

  public Marker getItem(int index) {
    return mItems.get(index);
  }

  public boolean add(Marker t) {
    return mItems.add(t);
  }

  /**
   * set the Marker to be displayed for this cluster
   */
  public void setMarker(Marker marker) {
    mMarker = marker;
  }

  /**
   * @return the Marker to be displayed for this cluster
   */
  public Marker getMarker() {
    return mMarker;
  }
}
