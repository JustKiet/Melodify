package com.melodify.music.constant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.melodify.music.MyApplication;
import com.melodify.music.model.Song;
import com.melodify.music.model.UserInfor;
import com.melodify.music.prefs.DataStoreManager;
import com.melodify.music.service.MusicReceiver;
import com.melodify.music.service.MusicService;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GlobalFunction {

    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void onClickOpenGmail(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", AboutUsConfig.GMAIL, null));
        context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    public static void onClickOpenSkype(Context context) {
        try {
            Uri skypeUri = Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat");
            context.getPackageManager().getPackageInfo("com.skype.raider", 0);
            Intent skypeIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
            skypeIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
            context.startActivity(skypeIntent);
        } catch (Exception e) {
            openSkypeWebview(context);
        }
    }

    private static void openSkypeWebview(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")));
        } catch (Exception exception) {
            String skypePackageName = "com.skype.raider";
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + skypePackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + skypePackageName)));
            }
        }
    }

    public static void onClickOpenFacebook(Context context) {
        Intent intent;
        try {
            String urlFacebook = AboutUsConfig.PAGE_FACEBOOK;
            PackageManager packageManager = context.getPackageManager();
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlFacebook = "fb://facewebmodal/f?href=" + AboutUsConfig.LINK_FACEBOOK;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_FACEBOOK));
        }
        context.startActivity(intent);
    }

    public static void onClickOpenYoutubeChannel(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_YOUTUBE)));
    }

    public static void onClickOpenZalo(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.ZALO_LINK)));
    }

    public static void callPhoneNumber(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + AboutUsConfig.PHONE_NUMBER));
                activity.startActivity(callIntent);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + AboutUsConfig.PHONE_NUMBER));
                activity.startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static void startMusicService(Context ctx, int action, int songPosition) {
        Intent musicService = new Intent(ctx, MusicService.class);
        musicService.putExtra(Constant.MUSIC_ACTION, action);
        musicService.putExtra(Constant.SONG_POSITION, songPosition);
        ctx.startService(musicService);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public static PendingIntent openMusicReceiver(Context ctx, int action) {
        Intent intent = new Intent(ctx, MusicReceiver.class);
        intent.putExtra(Constant.MUSIC_ACTION, action);

        int pendingFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        return PendingIntent.getBroadcast(ctx.getApplicationContext(), action, intent, pendingFlag);
    }

    public static boolean isFavoriteSong(Song song) {
        if (song.getFavorite() == null || song.getFavorite().isEmpty()) return false;
        List<UserInfor> listUsersFavorite = new ArrayList<>(song.getFavorite().values());
        if (listUsersFavorite.isEmpty()) return false;
        for (UserInfor userInfor : listUsersFavorite) {
            if (DataStoreManager.getUser().getEmail().equals(userInfor.getEmailUser())) {
                return true;
            }
        }
        return false;
    }

    public static void onClickFavoriteSong(Context context, Song song, boolean isFavorite) {
        if (context == null) return;
        if (isFavorite) {
            String userEmail = DataStoreManager.getUser().getEmail();
            UserInfor userInfor = new UserInfor(System.currentTimeMillis(), userEmail);
            MyApplication.get(context).getSongsDatabaseReference()
                    .child(String.valueOf(song.getId()))
                    .child("favorite")
                    .child(String.valueOf(userInfor.getId()))
                    .setValue(userInfor);
        } else {
            UserInfor userInfor = getUserFavoriteSong(song);
            if (userInfor != null) {
                MyApplication.get(context).getSongsDatabaseReference()
                        .child(String.valueOf(song.getId()))
                        .child("favorite")
                        .child(String.valueOf(userInfor.getId()))
                        .removeValue();
            }
        }
    }

    public static UserInfor getUserFavoriteSong(Song song) {
        UserInfor userInfor = null;
        if (song.getFavorite() == null || song.getFavorite().isEmpty()) return null;
        List<UserInfor> listUsersFavorite = new ArrayList<>(song.getFavorite().values());
        if (listUsersFavorite.isEmpty()) return null;
        for (UserInfor userObject : listUsersFavorite) {
            if (DataStoreManager.getUser().getEmail().equals(userObject.getEmailUser())) {
                userInfor = userObject;
                break;
            }
        }
        return userInfor;
    }
}