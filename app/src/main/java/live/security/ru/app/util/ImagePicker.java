package live.security.ru.app.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.graphics.TypefaceCompatUtil.getTempFile;
/**
 * @author sardor
 */
public class ImagePicker {

  public static Intent getFilePickIntent() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    return intent;
  }

  @SuppressLint("RestrictedApi")
  public static Intent getPickImageIntent(Context context) {
    Intent chooserIntent = null;

    List<Intent> intentList = new ArrayList<>();

    Intent pickIntent = new Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    takePhotoIntent.putExtra("return-data", true);
    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));
    intentList = addIntentsToList(context, intentList, pickIntent);
    intentList = addIntentsToList(context, intentList, takePhotoIntent);

    if (intentList.size() > 0) {
      chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
          "Pick image");
      chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
    }

    return chooserIntent;
  }

  private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
    List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
    for (ResolveInfo resolveInfo : resInfo) {
      String packageName = resolveInfo.activityInfo.packageName;
      Intent targetedIntent = new Intent(intent);
      targetedIntent.setPackage(packageName);
      list.add(targetedIntent);
    }
    return list;
  }

  @Nullable
  public static String getPath(Context context, Uri uri) {
    // DocumentProvider
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      } else if (isDownloadsDocument(uri)) {// DownloadsProvider
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        return getDataColumn(context, contentUri, null, null);

      } else if (isMediaDocument(uri)) { // MediaProvider
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{split[1]};
        return getDataColumn(context, contentUri, selection, selectionArgs);

      }
    } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
      // Return the remote address
      if (isGooglePhotosUri(uri))
        return uri.getLastPathSegment();
      return getDataColumn(context, uri, null, null);

    } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
      return uri.getPath();
    }
    return null;
  }

  private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {column};
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        final int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }

  private static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  private static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  private static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  private static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }
}
