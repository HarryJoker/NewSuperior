package gov.android.com.superior.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by wanghua on 17/8/12.
 */

public class FileReader {

    //读取文本文件中的内容
    public static String ReadTxtFile(Context context, String strFilePath) throws UnsupportedEncodingException {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        String fileCode = EncodingDetect.getJavaEncode(strFilePath);
        Logger.d("file encode code：" + fileCode);
        fileCode = fileCode != null && !"".equals(fileCode) ? fileCode : "UTF-8";
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_LONG).show();
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {

                    InputStreamReader inputreader = new InputStreamReader(instream, fileCode);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return new String(content.getBytes(fileCode), "UTF-8");
    }

    public static String readWord(String file){
        // 创建输入流读取doc文件
        FileInputStream in;
        String text = "";
        try {
            in = new FileInputStream(new File(file));
            // 创建WordExtractor
//            WordExtractor extractor = new WordExtractor();
            // 对doc文件进行提取
//            text = extractor.extractText(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text.trim().replace("/r", "");
    }

}
