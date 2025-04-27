package com.example.fnart_java_gallery;

import com.example.fnart_java_gallery.utils.ImageResizer;

public class ImageResizeMain {
    public static void main(String[] args) {
        System.out.println("Starting image resizing process...");
        ImageResizer.resizeAllImages();
        System.out.println("Image resizing completed!");
    }
} 