package com.oldering.kintone.higashi.model;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CybozuSetting {
    private static final String TAG = "CybozuSetting";

    // TODO(benoit) maybe some others fields to add here ?
    // also, certificateData is actually optional.
    private String domain = "";
    private String loginId = "";
    private byte[] certificateData = null;

    public boolean hasCertificate() {
        return this.certificateData != null;
    }

    public static CybozuSetting parseUri(Context context, Uri uri) {
        CybozuSetting cybozuSetting = new CybozuSetting();

        InputStream in = null;
        try {
            in = context.getContentResolver().openInputStream(uri);
            XmlPullParser xp = XmlPullParserFactory.newInstance().newPullParser();
            xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            xp.setInput(in, "utf-8");
            int eventType = xp.getEventType();
            String tmp;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tmp = xp.getName();
                    if (tmp.equalsIgnoreCase("connection")) {
                        cybozuSetting.setDomain(xp.getAttributeValue(null, "url"));
                        cybozuSetting.setLoginId(xp.getAttributeValue(null, "login_id"));
                    } else if (tmp.equalsIgnoreCase("client_certificate") && xp.nextToken() == XmlPullParser.TEXT) {
                        cybozuSetting.setCertificateData(Base64.decode(xp.getText().trim(), Base64.DEFAULT));
                    }
                }
                eventType = xp.nextToken();
            }
            // only those two are required
            if (cybozuSetting.getDomain().isEmpty() || cybozuSetting.getLoginId().isEmpty()) {
                Log.w(TAG, "parseUri: missing domain and or username" + cybozuSetting.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cybozuSetting;
    }
}
