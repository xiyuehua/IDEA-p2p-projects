package com.bjpowernode.springboot;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.transform.MatrixType;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:Test
 * Package:com.bjpowernode.springboot
 * Description
 *
 * @Date:2020/3/2711:09
 * @author:xyh
 */
public class Test {

    public static void main(String[] args) throws WriterException, IOException {
        Map<EncodeHintType, Object> encodeHintTypeObjectMap = new HashMap<EncodeHintType, Object>();
        encodeHintTypeObjectMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("weixin://wxpay/bizpayurl?pr=Mvasgua", BarcodeFormat.QR_CODE, 200, 200, encodeHintTypeObjectMap);

        Path path = FileSystems.getDefault().getPath("D://","grcode.png");

        //将矩阵对象转换为图片
        MatrixToImageWriter.writeToPath(bitMatrix, "png",path);
        System.out.println("生成二维码成功");
    }
}
