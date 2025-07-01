package com.atipls.chat.data;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.Hashtable;

import com.atipls.chat.State;
import com.atipls.chat.model.HasIcon;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Icons {
    private State s;
    private Hashtable<String, Bitmap> icons;
    private Hashtable<String, ArrayList<WeakReference<ImageView>>> imageViews;

    private ArrayList<String> iconHashes;
    private ArrayList<String> activeRequests;

    public Icons(State s) {
        this.s = s;
        icons = new Hashtable<String, Bitmap>();
        imageViews = new Hashtable<String, ArrayList<WeakReference<ImageView>>>();
        iconHashes = new ArrayList<String>();
        activeRequests = new ArrayList<String>();
    }

    public Bitmap get(HasIcon target) {
        if (s.iconType == State.ICON_TYPE_NONE)
            return null;

        String hash = target.getIconHash();
        if (hash == null)
            return null;

        Bitmap result = (Bitmap) icons.get(hash);
        if (result != null)
            return result;

        if (!activeRequests.contains(hash)) {
            activeRequests.add(hash);
            s.api.aFetchIcon(target, null);
        }

        return null;
    }

    public void removeRequest(String hash) {
        int index = activeRequests.indexOf(hash);
        if (index != -1)
            activeRequests.remove(index);
    }

    public void load(ImageView image, Drawable initial, HasIcon target) {
        image.setTag(null);
        final Bitmap maybeBitmap = get(target);
        if (maybeBitmap != null) {
            image.setImageBitmap(maybeBitmap);
            return;
        }

        image.setImageDrawable(initial);
        if (target.getIconHash() == null)
            return;

        if (!imageViews.containsKey(target.getIconHash())) {
            imageViews.put(target.getIconHash(), new ArrayList<WeakReference<ImageView>>());
        }

        ArrayList<WeakReference<ImageView>> views = imageViews.get(target.getIconHash());
        views.add(new WeakReference<ImageView>(image));

        image.setTag(target.getIconHash());

        if (!activeRequests.contains(target.getIconHash())) {
            activeRequests.add(target.getIconHash());
            s.api.aFetchIcon(target, null);
        }
    }

    public void set(final String hash, final Bitmap icon) {
        removeRequest(hash);

        if (!icons.containsKey(hash) && icons.size() >= 100) {
            String firstHash = (String) iconHashes.get(0);
            icons.remove(firstHash);
            iconHashes.remove(0);
        }

        icons.put(hash, icon);
        iconHashes.add(hash);

        if (!imageViews.containsKey(hash))
            return;

        s.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<WeakReference<ImageView>> references = imageViews.get(hash);
                imageViews.remove(hash);
                for (int i = 0; i < references.size(); i++) {
                    WeakReference<ImageView> ref = references.get(i);
                    if (ref == null)
                        continue;

                    ImageView imageView = ref.get();
                    if (imageView != null && imageView.getTag() != null && imageView.getTag().equals(hash)) {
                        imageView.setImageBitmap(icon);
                        imageView.setTag(null);
                        imageView.invalidate();
                    }
                }
            }
        });
    }
}