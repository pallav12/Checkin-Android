package com.alcatraz.admin.project_alcatraz.Session;

import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.util.Dictionary;
import java.util.List;

/**
 * Created by shivanshs9 on 6/5/18.
 */

public class MenuItem {
    protected String name;
    protected Dictionary cost;
    protected String quantity;
    protected Bitmap image;
    protected MediaStore.Video video;
    protected List<MenuItem> items;
}
