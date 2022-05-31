package com.jiace.apm.until;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


import com.jiace.apm.Application;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.WINDOW_SERVICE;

public class Utils {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String FILE_TIME_FORMAT = "yyyyMMdd_HHmmssSSS";
    private static final String TIME_FORMAT_MIN = "mm:ss";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String GREEN_TIME_FORMAT = "E MMM dd HH:mm:ss z yyyy";


    private static final ThreadLocal<DateFormat> mDateTimeFormatThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<DateFormat> mDateFormatThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<DateFormat> mTimeFormatThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<DateFormat> mFileTimeFormatThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<DateFormat> mDateTimeTFormatThreadLocal = new ThreadLocal<>();

    private Utils() { }

    public static DateFormat getDateTimeFormat() {
        DateFormat df = mDateTimeFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
            mDateTimeFormatThreadLocal.set(df);
        }

        return df;
    }

    private static DateFormat getDateTimeTFormat() {
        DateFormat df = mDateTimeTFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
            mDateTimeTFormatThreadLocal.set(df);
        }
        return df;
    }

    private static DateFormat getDateFormat() {
        DateFormat df = mDateFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            mDateFormatThreadLocal.set(df);
        }

        return df;
    }

    private static DateFormat getTimeFormat() {
        DateFormat df = mTimeFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
            mTimeFormatThreadLocal.set(df);
        }

        return df;
    }

    public static Date parseDataFromString(String dateString) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date date = null;
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private static DateFormat getFileTimeFormat() {
        DateFormat df = mFileTimeFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(FILE_TIME_FORMAT, Locale.getDefault());
            mFileTimeFormatThreadLocal.set(df);
        }
        return df;
    }

    private static DateFormat getGreenTimeFormat () {
        DateFormat df = mFileTimeFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(GREEN_TIME_FORMAT,Locale.getDefault());
            mFileTimeFormatThreadLocal.set(df);
        }

        return df;
    }


    public static String formatDateTime(Date time) {
        return getDateTimeFormat().format(time);
    }

    public static String formatDate(Date time) {
        return getDateFormat().format(time);
    }

    public static String formatTime(Date time) {
        return getTimeFormat().format(time);
    }

    public static String formatTTime(Date time) {
        return getDateTimeTFormat().format(time);
    }

    public static String formatMinTime(Date min) {
        return getMinFormat().format(min);
    }

    public static String formatHourMinTime(Date min) {
        return getHourMinFormat().format(min);
    }

    public static Date parseGreenDate(String date) {
        try {
            return getDateTimeTFormat().parse(date);
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }
    }

    public static String formatLongToTime(int t) {
        if (t < 0) {
            return "00:00";
        }
        int minute;
        int second;
        if (t >= 60) {
            minute = t / 60;
            second = t % 60;
            return String.format("%s:%s", formatInt(minute), formatInt(second));
        } else {
            second = t;
            return String.format("00:%s", formatInt(second));
        }
    }

    private static String formatInt(int value) {
        if (value / 10 == 0) {
            return String.format("0%s", value);
        } else {
            return String.format("%s", value);
        }
    }

    private static DateFormat getMinFormat() {
        DateFormat df = mTimeFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(TIME_FORMAT_MIN, Locale.getDefault());
            mTimeFormatThreadLocal.set(df);
        }

        return df;
    }

    private static DateFormat getHourMinFormat() {
        DateFormat df = mTimeFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
            mTimeFormatThreadLocal.set(df);
        }
        return df;
    }

    public static String arrayToString(String[] strings) {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < strings.length; i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    public static int findIndexInArray(String[] items, String item) {
        for (int i = 0; i < items.length; i++)
            if (items[i].equals(item))
                return i;
        return -1;
    }

    public static int stringToInt(String str) {
        return stringToInt(str, 0);
    }

    public static int stringToInt(String str, int defaultValue) {
        if ((str != null) && !str.isEmpty()) {
            try {
                return Integer.parseInt(str);
            } catch (Exception ignore) {
            }
        }
        return defaultValue;
    }

    public static float stringToFloat(String str) {
        return stringToFloat(str, 0f);
    }

    public static float stringToFloat(String str, float defaultValue) {
        if ((str != null) && !str.isEmpty()) {
            try {
                return Float.parseFloat(str);
            } catch (Exception ignore) {

            }
        }
        return defaultValue;
    }

    public static double stringToDouble(String str) {
        return stringToDouble(str, 0D);
    }

    public static double stringToDouble(String str, double defaultValue) {
        if ((str != null) && !str.isEmpty()) {
            try {
                return Double.parseDouble(str.trim());
            } catch (Exception ignore) {

            }
        }
        return defaultValue;
    }

    public static String getMD5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            byte[] hash = md5.digest();

            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10)
                    hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception ignore) {

        }
        return "";
    }

    /**
     * 将小数度数转换为度分秒格式
     */
    public static String convertGpsToRational(double num) {
        int degrees = (int) Math.floor(num);
        num = (num - degrees) * 60;
        int minutes = (int) Math.floor(num);
        num = (num - minutes) * 60;
        int seconds = (int) Math.round(num * 1000);
        return String.format(Locale.CHINA,"%d/1,%d/1,%d/1000", degrees, minutes, seconds);
    }

    public static byte[] stringToByte(String str, int length) {
        try {
            if (length != 0) {
                byte[] bytes = new byte[length];
                byte[] data = str.getBytes(Charset.forName("GB2312"));
                System.arraycopy(data, 0, bytes, 0, Math.min(data.length, length));
                bytes[length - 1] = 0x00;
                return bytes;
            } else {
                byte[] strBytes = str.getBytes(Charset.forName("GB2312"));
                strBytes[strBytes.length - 1] = 0x00;
                return strBytes;
            }
        } catch (Exception e) {
            if (length != 0) {
                byte[] bytes = new byte[length];
                byte[] data = str.getBytes();
                System.arraycopy(data, 0, bytes, 0, Math.min(data.length, length));
                bytes[length - 1] = 0x00;
                return bytes;
            } else {
                byte[] strBytes = str.getBytes(Charset.forName("GB2312"));
                strBytes[strBytes.length - 1] = 0x00;
                return strBytes;
            }
        }
    }

    /**
     * 将字符串转换为长度为length的字节数组,如果length为0,按照实际字节数进行转换,
     * 否则,超出,就截取,不足,补0
     * @param str 需要转换为字节数组的字符串
     * @param length 字节数组长度
     * @return
     */
    public static byte[] stringToBytes(String str, int length) {
        try {
            if (length != 0) {
                byte[] bytes = new byte[length];
                byte[] data = str.getBytes(Charset.forName("GB2312"));
                System.arraycopy(data, 0, bytes, 0, Math.min(data.length, length));
                return bytes;
            } else {
                return str.getBytes(Charset.forName("GB2312"));
            }
        } catch (Exception e) {
            if (length != 0) {
                byte[] bytes = new byte[length];
                byte[] data = str.getBytes();
                System.arraycopy(data, 0, bytes, 0, Math.min(data.length, length));
                return bytes;
            } else {
                return str.getBytes(Charset.forName("GB2312"));
            }
        }
    }


    public static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value & 0XFF),
                (byte) ((value >> 8) & 0XFF),
                (byte) ((value >> 16) & 0XFF),
                (byte) ((value >> 24) & 0XFF)
        };
    }

    public static byte[] longToBytes(long value) {
        return new byte[]{
                (byte) (value & 0XFF),
                (byte) ((value >> 8) & 0XFF),
                (byte) ((value >> 16) & 0XFF),
                (byte) ((value >> 24) & 0XFF),
                (byte) ((value >> 32) & 0XFF),
                (byte) ((value >> 40) & 0XFF),
                (byte) ((value >> 48) & 0XFF),
                (byte) ((value >> 56) & 0XFF),
        };
    }

    public static byte[] shortToBytes(Short value) {
        return new byte[]{
                (byte) (value & 0XFF),
                (byte) ((value >> 8) & 0XFF)
        };
    }

    public static byte[] u24IntToBytes(int value) {
        return new byte[]{
                (byte) (value & 0XFF),
                (byte) ((value >> 8) & 0XFF),
                (byte) ((value >> 16) & 0XFF)
        };
    }

    public static byte[] getSingleByteArray(int a) {
        byte[] data = new byte[1];
        data[0] = (byte) a;
        return data;
    }

    public static byte[] floatToBytes(float value) {
        int a = Float.floatToRawIntBits(value);
        return new byte[]{
                (byte) (a & 0XFF),
                (byte) ((a >> 8) & 0XFF),
                (byte) ((a >> 16) & 0XFF),
                (byte) ((a >> 24) & 0XFF)
        };
    }

    public static byte[] doubleToBytes(double value) {
        long a = Double.doubleToRawLongBits(value);
        return new byte[]{
                (byte) (a & 0XFF),
                (byte) ((a >> 8) & 0XFF),
                (byte) ((a >> 16) & 0XFF),
                (byte) ((a >> 24) & 0XFF),
                (byte) ((a >> 32) & 0XFF),
                (byte) ((a >> 40) & 0XFF),
                (byte) ((a >> 48) & 0XFF),
                (byte) ((a >> 56) & 0XFF),
        };
    }

    public static String bytesToString(byte[] bytes) {
        return bytesToString(bytes, 0, bytes.length);
    }

    public static String bytesToString(byte[] bytes, int offset) {
        return bytesToString(bytes, offset, bytes.length - offset);
    }

    public static String bytesToString(byte[] bytes, int offset, int maxLength) {
        if ((bytes == null) || (bytes.length == 0) || (bytes.length <= offset) || (maxLength <= 0)) {
            return "";
        } else {
            int end = (offset + maxLength > bytes.length) ? (bytes.length - offset) : (offset + maxLength);
            for (int i = offset; i < end; i++) {
                if (bytes[i] == 0) {
                    end = i;
                    break;
                }
            }
            if (end == offset)
                return "";
            byte[] tmp = new byte[end - offset];
            System.arraycopy(bytes, offset, tmp, 0, tmp.length);
            Charset charset =  Charset.forName("GB2312");
            return new String(tmp, charset);
        }
    }

    public static int bytesToUnsignedShort(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) + ((bytes[offset + 1] & 0xFF) << 8);
    }

    public static int bytesToShort(byte[] bytes, int offset) {
        ByteBuffer bf = ByteBuffer.allocate(bytes.length);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        for (byte aByte : bytes) {
            bf.put(aByte);
        }
        return bf.getShort(offset);
    }

    public static int bytesToShort(byte[] bytes) {
        return bytesToShort(bytes, 0);
    }

    public static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes, 0);
    }

    public static int bytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
                | ((bytes[offset + 2] & 0xFF) << 16)
                | ((bytes[offset + 3] & 0xFF) << 24));
    }

    public static int bytesTo24UInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
                | ((bytes[offset + 2] & 0xFF) << 16));
    }

    public static long bytesToLong(byte[] bytes, int offset){
        long ret = 0;
        for(int i=0; i<8; i++){
            ret <<=8;
            ret |=(bytes[i + offset] & 0x00000000000000FF);
        }
        return ret;
    }

    public static long bytesToLong(byte[] bytes){
        return bytesToLong(bytes, 0);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static double getLeftData(double value) {
        int a = (int) value;
        return value - a;
    }

    public static int localToUTC(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time + 8 * 60 * 60 * 1000);
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static int getDisPlayWight(Context context) {
        WindowManager manager;
        manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getDisPlayHight(Context context) {
        WindowManager manager;
        manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 检测定位功能是否打开
     */
    public static boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return (networkProvider || gpsProvider);
    }

    public static String getRandomId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return
     * @throws Exception
     */
    public static String hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        return Base64.encodeToString(mac.doFinal(text), Base64.NO_WRAP);
    }

    public static int randomNum() {
        int max = 90;
        int min = 10;
        return randomNum(max, min);
    }

    public static int randomNum(int max, int min) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static String floatTString(@NotNull float value) {
        return String.format(Locale.CHINA, "%.1f", value);
    }

    public static boolean isOTGHostEnable(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        return usbManager.getDeviceList().size() != 0;
    }


    /**
     * 禁止关闭对话框
     */
    public static void setDisableCloseDialog(final DialogInterface dlg)
    {
        if (dlg == null)
            return;

        try
        {
            Field field = dlg.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);

            // 将 mShowing 变量设为 false，系统会认为对话框已关闭
            field.set(dlg, false);
        }
        catch (Exception ignore)
        {
        }
    }

    /**
     * 允许关闭对话框
     */
    public static void setEnableCloseDialog(final DialogInterface dlg)
    {
        if (dlg == null)
            return;

        try
        {
            Field field = dlg.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);

            // 将 mShowing 变量设为 false，系统会认为对话框已关闭
            field.set(dlg, true);
        }
        catch (Exception ignore)
        {
        }
    }

    /**
     * 图片闪烁
     * @param imageView
     */
    public static void setFlickerAnimation(ImageView imageView)
    {
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        imageView.setAnimation(animation);
    }

    /**
     * 休眠
     * @param time 毫秒(ms)
     */
    public static void sleep(long time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException ignore)
        {
        }
    }

    /**
     * 保存崩溃信息
     * @param ex
     */
  /*  public static void log(Throwable ex)
    {
        try (FileWriter writer = new FileWriter(JYHApplication.Companion.getCrashLogFile(), true))
        {
            writer.write("--------------------------  ");
            writer.write(Utils.formatDateTime(new Date()));
            writer.write("  --------------------------\r\n");

            String threadName = Thread.currentThread().getName();
            if (threadName != null)
                writer.write(threadName);
            writer.write("\r\n");

            writer.write(Log.getStackTraceString(ex));
            writer.write("\r\n");
        }
        catch (Exception ignore)
        {
        }
    }*/

    /**
     * 使用字符串将链表连成一个字符串
     * @param list
     * @param delimiter 分隔符
     * @return
     */
    public static String join(ArrayList<String> list, String delimiter)
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<list.size(); i++)
        {
            if (list.get(i).isEmpty()) {
                continue;
            }

            if(i != 0)
            {
                sb.append(delimiter);
            }
            sb.append(list.get(i));
        }
        return  sb.toString();
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static File makeZipFiles(File dir,File[] files) throws IOException
    {
        if (files.length == 0)
            return null;
        File zipFile = new File(dir,String.format("%s.zip",dir.getName()));
        if (zipFile.exists())
        {
            if (!zipFile.delete())
                return null;

            if (!zipFile.createNewFile())
            {
                return null;
            }
        }

        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
        for (File f : files)
        {
            if (f != null)
            {
                FileInputStream inputStream = new FileInputStream(f);
                zipOutputStream.putNextEntry(new ZipEntry(f.getName()));
                byte[] buffer = new byte[256];
                while (inputStream.read(buffer) != -1)
                {
                    zipOutputStream.write(buffer,0,buffer.length);
                    zipOutputStream.flush();
                }
                inputStream.close();
            }
        }
        zipOutputStream.close();

        return zipFile;
    }

    /**
     * 检测当前的网络状态
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) Application.Companion.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            return ((info != null) && info.isConnected() && (info.getState() == NetworkInfo.State.CONNECTED));
        }
        return false;
    }

    /**
     * 是否为IPv4地址
     * @param ip
     * @return
     */
    public static boolean isIPv4Address(String ip) {
        boolean ret = false;
        try {
            if (ip.contains(".")) {
                String[] list = ip.split("\\.");
                if (list.length == 4) {
                    ret = true;
                    for (String one : list) {
                        int value = Integer.valueOf(one);
                        if (value <0 ||
                        value >255) {
                            ret = false;
                            break;
                        }
                    }
                }
            }
        }catch (Exception e) {
            ret = false;
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * 获取SD卡目录
     * */
    public static String getSDCardPath(Context context)  {
        String sdCardPath = null;
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
            Class<?> volumeClass = Class.forName("android.os.storage.StorageVolume");
            Method getPath = volumeClass.getDeclaredMethod("getDirectory");
            Method isRemovable = volumeClass.getDeclaredMethod("isRemovable");
            getPath.setAccessible(true);
            isRemovable.setAccessible(true);

            for (int i = 0; i < storageVolumes.size(); i++) {
                StorageVolume storageVolume = storageVolumes.get(i);
                String path = getPath.invoke(storageVolume).toString();
                boolean isRemove = (boolean) isRemovable.invoke(storageVolume);
                if (isRemove) {
                    sdCardPath = path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("sd = " + sdCardPath);
        return sdCardPath;
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
