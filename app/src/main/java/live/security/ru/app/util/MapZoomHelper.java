package live.security.ru.app.util;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

/**
 * @author sardor
 */
public class MapZoomHelper {
  //Custom implementation of MapView.zoomToBoundingBox(): setting the found zoom - 2, as the current
  //zoom level is a bit close and doesn't cover all points
  public static void zoomToBoundingBox(MapView mapView, BoundingBox boundingBox, boolean animated) {
    int width = mapView.getWidth();
    int height = mapView.getHeight();
    int borderSizeInPixels = 50;

    double nextZoom = Double.MIN_VALUE;//TileSystem.getBoundingBoxZoom(boundingBox, width - 2 * borderSizeInPixels, height - 2 * borderSizeInPixels);


    if (nextZoom == Double.MIN_VALUE) {
      return;
    }
    nextZoom = Math.min(mapView.getMaxZoomLevel(), Math.max(nextZoom, mapView.getMinZoomLevel()));
    final IGeoPoint center = boundingBox.getCenterWithDateLine();

    LoggingTool.Companion.log("First zoom " + nextZoom);
//    if (nextZoom >= 2) {
//      nextZoom = nextZoom - 2;
//    }
    LoggingTool.Companion.log("Second zoom " + nextZoom);

    if (animated) { // it's best to set the center first, because the animation is not immediate
      // in a perfect world there would be an animation for both center and zoom level
      mapView.getController().setCenter(center);
      mapView.getController().zoomTo(nextZoom);
    } else { // it's best to set the zoom first, so that the center is accurate
      mapView.getController().setZoom(nextZoom);
      mapView.getController().setCenter(center);
    }
  }
}
