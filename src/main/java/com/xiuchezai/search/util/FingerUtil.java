package com.xiuchezai.search.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hoo
 */
public class FingerUtil {
    public static final String DES_ALGORITHM = "DES";
    public static final String CIPHER_DES_ALGORITHM = "DES/CBC/PKCS5Padding";//*DES/ECB/NoPadding
    public static final String DES_IV = "90AbcDeF";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String CIPHER_RSA_ALGORITHM = "RSA/None/PKCS1Padding";
    public static final String PROVIDER = "BC";
    public static final String SIGNATURE_SHA1_ALGORITHM = "SHA1WithRSA";
    public static final String SIGNATURE_SHA256_ALGORITHM = "SHA256WithRSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * JDK支持HmacMD5, HmacSHA1,HmacSHA256, HmacSHA384, HmacSHA512
     */
    public enum HmacType {
        HmacMD5, HmacSHA1, HmacSHA256, HmacSHA384, HmacSHA512;
    }

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * AES解密
     *
     * @param password
     * @param key
     * @return
     */
    public static String decryptAES(String password, String key) {
        try {
            //判断Key是否为16位
            if (key == null || key.length() != 16) {
                throw new InvalidKeyException("密钥必须不空且为16位");
            }
            //判断Key是否正确
            if (password == null) {
                return null;
            }
            //算法/模式/补码方式
            //Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("ASCII"), "AES"));
            byte[] bytes = hex2byte(password);
            if (bytes == null) {
                return null;
            }
            return new String(cipher.doFinal(bytes), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES加密
     *
     * @param source
     * @param key
     * @return
     */
    public static String encryptAES(String source, String key) {
        try {
            //判断Key是否为16位
            if (key == null || key.length() != 16) {
                throw new InvalidKeyException("密钥必须不空且为16位");
            }
            if (source == null) {
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("ASCII"), "AES"));
            return byte2hex(cipher.doFinal(source.getBytes())).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 十六制转二进制
     *
     * @param hex
     * @return
     */
    public static byte[] hex2byte(String hex) {
        if (hex == null) {
            return null;
        }
        int len = hex.length();
        if (len % 2 == 1) {
            return null;
        }
        byte[] b = new byte[len / 2];
        for (int i = 0; i != len / 2; i++) {
            b[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    /**
     * 二进制转十六制
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder("");
        String hex = "";
        for (int n = 0; n < b.length; n++) {
            hex = (Integer.toHexString(b[n] & 0XFF));
            if (hex.length() == 1) {
                hs.append("0").append(hex);
            } else {
                hs.append(hex);
            }
        }
        return hs.toString().toUpperCase();
    }

    /**
     * MD5加密
     *
     * @param source 要加密码的字符串
     * @return String 加密之后的字符串
     */
    public static String md5(String source) {
        return md5(source, "utf-8");
    }

    /**
     * MD5加密
     *
     * @param source 要加密码的字符串
     * @return String 加密之后的字符串
     */
    public static String md5(String source, String charset) {
        try {
            if (source == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder(32);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = null;
            if (charset == null || "".equals(charset)) {
                bytes = md.digest(source.getBytes());
            } else {
                bytes = md.digest(source.getBytes(charset));
            }
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
            }

            return sb.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * DES加密
     *
     * @param source    待加密原文
     * @param secretKey 加密密钥
     * @param charset   字符集
     * @return
     */
    public static String encryptDes(String source, String secretKey, String charset) {
        if (source == null || secretKey == null || charset == null) {
            return null;
        }
        if (secretKey.length() % 8 != 0) {
            return null;
        }
        try {
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(CIPHER_DES_ALGORITHM);
            //ECB模式下，iv不需要
            //cipher.init(Cipher.ENCRYPT_MODE, generateDesKey(secretKey, charset));
            //用密匙初始化Cipher对象,DECRYPT_MODE用于将Cipher初始化为解密模式的常量
            IvParameterSpec iv = new IvParameterSpec(DES_IV.getBytes(charset));
            cipher.init(Cipher.ENCRYPT_MODE, generateDesKey(secretKey, charset), iv);
            //正式执行加密操作,按单部分操作加密或解密数据，或者结束一个多部分操作
            byte[] bytes = cipher.doFinal(source.getBytes(charset));
            return encryptByteBase64(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DES解密
     *
     * @param password  待解密密文
     * @param secretKey 解密密钥
     * @param charset   字符集
     * @return
     */
    public static String decryptDes(String password, String secretKey, String charset) {
        if (password == null || secretKey == null || charset == null) {
            return null;
        }
        if (secretKey.length() % 8 != 0) {
            return null;
        }
        try {
            //Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(CIPHER_DES_ALGORITHM);
            //ECB模式下，iv不需要
            //cipher.init(Cipher.DECRYPT_MODE, generateDesKey(secretKey, charset));
            //用密匙初始化Cipher对象,DECRYPT_MODE用于将Cipher初始化为解密模式的常量
            IvParameterSpec iv = new IvParameterSpec(DES_IV.getBytes(charset));
            cipher.init(Cipher.DECRYPT_MODE, generateDesKey(secretKey, charset), iv);
            //正式执行加密操作,按单部分操作加密或解密数据，或者结束一个多部分操作
            byte[] byteBase64 = decryptByteBase64(password);
            byte[] bytes = cipher.doFinal(byteBase64);
            return new String(bytes, charset);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得秘密密钥
     *
     * @param secretKey
     * @return
     * @throws NoSuchAlgorithmException,UnsupportedEncodingException
     */
    private static SecretKey generateDesKey(String secretKey, String charset)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, InvalidKeySpecException {
//        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//        secureRandom.setSeed(secretKey.getBytes(charset));
//        //为我们选择的DES算法生成一个KeyGenerator对象
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(DES_ALGORITHM);
//        keyGenerator.init(secureRandom);
//        // 生成密钥
//        return keyGenerator.generateKey();

        DESKeySpec keySpec = new DESKeySpec(secretKey.getBytes(charset));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
        return keyFactory.generateSecret(keySpec);
    }

    public static String hMac(HmacType hmacType, String source, String secretKey, String charset) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(charset), hmacType.name());
            Mac mac = Mac.getInstance(keySpec.getAlgorithm());
            mac.init(keySpec);
            byte[] bytes = mac.doFinal(source.getBytes(charset));
            return encryptByteBase64(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String algorithm, String source, String charset) {
        try {
            if (source == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = null;
            if (charset == null || "".equals(charset)) {
                bytes = md.digest(source.getBytes());
            } else {
                bytes = md.digest(source.getBytes(charset));
            }
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String algorithm, String source) {
        return encrypt(algorithm, source, "utf-8");
    }

    public static String decryptBase64(String password, String charset) {
        try {
            if (password == null) {
                return null;
            }
            String source = null;
            if (charset == null || "".equals(charset)) {
                source = new String(new BASE64Decoder().decodeBuffer(password));
            } else {
                source = new String(new BASE64Decoder().decodeBuffer(password), charset);
            }
            return source;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * base64解码
     *
     * @param password
     * @return
     */
    public static String decryptBase64(String password) {
        return decryptBase64(password, "utf-8");
    }

    public static byte[] decryptByteBase64(String password) {
        try {
            if (password == null) {
                return null;
            }
            byte[] source = new BASE64Decoder().decodeBuffer(password);
            return source;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptBase64(String source, String charset) {
        try {
            if (source == null) {
                return null;
            }
            String password = null;
            if (charset == null || "".equals(charset)) {
                password = new BASE64Encoder().encodeBuffer(source.getBytes());
            } else {
                password = new BASE64Encoder().encodeBuffer(source.getBytes(charset));
            }
            return password;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * base64编码
     *
     * @param source
     * @return
     */
    public static String encryptBase64(String source) {
        return encryptBase64(source, "utf-8");
    }

    public static String encryptByteBase64(byte[] source) {
        try {
            if (source == null) {
                return null;
            }
            String password = new BASE64Encoder().encodeBuffer(source);
            return password;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @param charset
     * @return
     */
    public static String signRSA(String data, String privateKey, String charset) {
        return signRSA(0, data, privateKey, charset);
    }

    /**
     * @param sign_type  签名类型
     * @param data       加密数据
     * @param privateKey 私钥
     * @param charset
     * @return
     */
    public static String signRSA(int sign_type, String data, String privateKey, String charset) {
        try {
            if (data == null) {
                return null;
            }
            // 取私钥对象
            PrivateKey key = getPrivateKey(privateKey, charset);
            if (key == null) {
                return null;
            }

            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(sign_type == 0 ? SIGNATURE_SHA1_ALGORITHM : SIGNATURE_SHA256_ALGORITHM);
            signature.initSign(key);
            signature.update(data.getBytes(charset));

            return encryptByteBase64(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     */
    public static boolean verifyRSA(String data, String publicKey, String sign, String charset) {
        return verifyRSA(0, data, publicKey, sign, charset);
    }

    /**
     * @param sign_type 签名类型
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return
     */
    public static boolean verifyRSA(int sign_type, String data, String publicKey, String sign, String charset) {
        try {
            if (data == null) {
                return false;
            }
            // 取公钥匙对象
            PublicKey key = getPublicKey(publicKey, charset);
            if (key == null) {
                return false;
            }

            Signature signature = Signature.getInstance(sign_type == 0 ? SIGNATURE_SHA1_ALGORITHM : SIGNATURE_SHA256_ALGORITHM);
            signature.initVerify(key);
            signature.update(data.getBytes(charset));

            //解密由base64编码的签名
            byte[] signs = decryptByteBase64(sign);
            // 验证签名是否正常
            return signature.verify(signs);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String decryptCipher(Key key, String data, String charset) {
        try {
            if (data == null || key == null) {
                return null;
            }
            // 对数据解密
            Cipher cipher = Cipher.getInstance(CIPHER_RSA_ALGORITHM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, key);

            InputStream ins = new ByteArrayInputStream(decryptByteBase64(data));
            ByteArrayOutputStream outs = new ByteArrayOutputStream();
            //rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
            byte[] buf = new byte[MAX_DECRYPT_BLOCK];
            int len = -1;
            while ((len = ins.read(buf)) != -1) {
                byte[] block = null;
                if (buf.length == len) {
                    block = buf;
                } else {
                    block = new byte[len];
                    for (int i = 0; i < len; i++) {
                        block[i] = buf[i];
                    }
                }
                outs.write(cipher.doFinal(block));
            }
            String source = new String(outs.toByteArray(), charset);
            ins.close();
            outs.close();
            return source;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptCipher(Key key, String data, String charset) {
        try {
            if (data == null || key == null) {
                return null;
            }
            // 对数据解密
            Cipher cipher = Cipher.getInstance(CIPHER_RSA_ALGORITHM, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            InputStream ins = new ByteArrayInputStream(data.getBytes(charset));
            ByteArrayOutputStream outs = new ByteArrayOutputStream();
            //rsa加密的字节大小最多是117，将需要加密的内容，按117位拆开加密
            byte[] buf = new byte[MAX_ENCRYPT_BLOCK];
            int len = -1;
            while ((len = ins.read(buf)) != -1) {
                byte[] block = null;
                if (buf.length == len) {
                    block = buf;
                } else {
                    block = new byte[len];
                    for (int i = 0; i < len; i++) {
                        block[i] = buf[i];
                    }
                }
                outs.write(cipher.doFinal(block));
            }

            byte[] bytes = outs.toByteArray();
            ins.close();
            outs.close();
            return encryptByteBase64(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥解密
     *
     * @param data
     * @param publicKey
     * @param charset
     * @return
     */
    public static String decryptByPublicKey(String data, String publicKey, String charset) {
        try {
            if (data == null || publicKey == null || charset == null) {
                return null;
            }
            PublicKey key = getPublicKey(publicKey, charset);
            if (key == null) {
                return null;
            }

            return decryptCipher(key, data, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥加密
     *
     * @param data
     * @param publicKey
     * @param charset
     * @return
     */
    public static String encryptByPublicKey(String data, String publicKey, String charset) {
        try {
            if (data == null || publicKey == null || charset == null) {
                return null;
            }
            PublicKey key = getPublicKey(publicKey, charset);
            if (key == null) {
                return null;
            }

            return encryptCipher(key, data, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取解码x509格式的公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @param charset
     */
    public static PublicKey getPublicKey(String publicKey, String charset) {
        try {
            if (publicKey == null || charset == null) {
                return null;
            }
            // 对密钥Base64解码
            byte[] keyBytes = decryptByteBase64(publicKey);

            // 构造X509EncodedKeySpec对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            // RSA_ALGORITHM 指定的加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM, PROVIDER);
            // 取公钥匙对象
            PublicKey key = keyFactory.generatePublic(x509KeySpec);

            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     *
     * @param data
     * @param privateKey
     * @param charset
     * @return
     */
    public static String decryptByPrivateKey(String data, String privateKey, String charset) {
        try {
            if (data == null || privateKey == null || charset == null) {
                return null;
            }
            PrivateKey key = getPrivateKey(privateKey, charset);
            if (key == null) {
                return null;
            }

            return decryptCipher(key, data, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥加密
     *
     * @param data
     * @param privateKey
     * @param charset
     * @return
     */
    public static String encryptByPrivateKey(String data, String privateKey, String charset) {
        try {
            if (data == null || privateKey == null || charset == null) {
                return null;
            }
            PrivateKey key = getPrivateKey(privateKey, charset);
            if (key == null) {
                return null;
            }

            return encryptCipher(key, data, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取解码pkcs格式的私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @param charset
     */
    public static PrivateKey getPrivateKey(String privateKey, String charset) {
        try {
            if (privateKey == null || charset == null) {
                return null;
            }
            // 对密钥Base64解码
            byte[] keyBytes = decryptByteBase64(privateKey);

            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            // RSA_ALGORITHM 指定的加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM, PROVIDER);
            // 取私钥对象
            PrivateKey key = keyFactory.generatePrivate(pkcs8KeySpec);

            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 初始化密钥
     *
     * @return
     */
    public static Map<String, Key> initRSAKey() {
        return initRSAKey(1);
    }

    /**
     * @param keySizeMultiple 1024的倍数
     * @return
     */
    public static Map<String, Key> initRSAKey(int keySizeMultiple) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM, PROVIDER);
            SecureRandom random = new SecureRandom();
            keyPairGen.initialize(1024 * keySizeMultiple, random);

            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 公钥
            Key publicKey = keyPair.getPublic();
            // 私钥
            Key privateKey = keyPair.getPrivate();

            Map<String, Key> keyMap = new HashMap<String, Key>(2);

            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 取得公钥，并进行base64编码
     *
     * @param keyMap
     * @return
     */
    public static String getPublicKey(Map<String, Key> keyMap) {
        try {
            Key key = keyMap.get(PUBLIC_KEY);
            if (key == null) {
                return null;
            }
            return encryptByteBase64(key.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 取得私钥，并进行base64编码
     *
     * @param keyMap
     * @return
     */
    public static String getPrivateKey(Map<String, Key> keyMap) {
        try {
            Key key = keyMap.get(PRIVATE_KEY);
            if (key == null) {
                return null;
            }
            return encryptByteBase64(key.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}