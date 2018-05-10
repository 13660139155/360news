package com.example.a360news.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;

import com.example.a360news.MyApplication;
import com.example.a360news.json.Data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by asus on 2018/4/25.
 */

public class FileDatabase {

    /**
     * 在本地文件保存图片
     * @param fileName 文件名
     * @param bitmap 图片
     */
    public static void saveBitmap(String fileName, Bitmap bitmap){
        String newUrl = fileName.replace("/", "");
        BufferedOutputStream bufferedOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = MyApplication.getContext().openFileOutput(newUrl, Context.MODE_PRIVATE);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 在本地文件取出保存的图片
     * @param fileName 文件名
     * @return 图片
     */
    public static Bitmap loadBitmap(String fileName){
        String newUrl = fileName.replace("/", "");
        BufferedInputStream bufferedInputStream = null;
        Bitmap bitmap = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = MyApplication.getContext().openFileInput(newUrl);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 在本地文件删除图片
     * @param fileName
     * @return
     */
    public static boolean deleteBitmap(String fileName){
        String newUrl = fileName.replace("/", "");
        File file = new File(MyApplication.getContext().getFilesDir(), newUrl);
        if(file.exists()){
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 保存新闻对象到本地文件
     */
    public static void saveInFile(Data data, String fileName){
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = MyApplication.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            fileOutputStream.close();
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取新闻对象从本地文件
     */
    public static Data loadFromFile(String fileName){
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = MyApplication.getContext().openFileInput(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
            return  (Data)objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(objectInputStream != null && fileInputStream != null){
                    fileInputStream.close();
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 删除新闻对象从本地
     */
    public static Boolean deleteData(String fileName){
        File file = new File(MyApplication.getContext().getFilesDir(), fileName);
        if(file.exists()){
            file.delete();
            return true;
        }
        return false;
    }
}
