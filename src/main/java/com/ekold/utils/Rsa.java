package com.ekold.utils;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Rsa {
    public static class Keys {
        private String privateKey;
        private String publicKey;

        public Keys(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }
    }

    public static class Generator {
        public static Keys generate() {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
                generator.initialize(2048, new SecureRandom());
                KeyPair pair = generator.generateKeyPair();
                PublicKey publicKey = pair.getPublic();
                PrivateKey privateKey = pair.getPrivate();
                return new Keys(new String(Base64.encode(privateKey.getEncoded())), new String(Base64.encode(publicKey
                        .getEncoded())));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class Encoder {
        private PrivateKey mPrivateKey;
        private Cipher cipher;

        public Encoder(String privateKey) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey.getBytes()));
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                mPrivateKey = keyFactory.generatePrivate(privatePKCS8);
                cipher = Cipher.getInstance("RSA", "BC");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String encode(String source) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, mPrivateKey);
                byte[] cipherText = cipher.doFinal(source.getBytes("utf-8"));
                return new String(Base64.encode(cipherText));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class Decoder {
        private PublicKey mPublicKey;
        private Cipher cipher;

        public Decoder(String publicKey) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            X509EncodedKeySpec publicX509 = new X509EncodedKeySpec(Base64.decode(publicKey.getBytes()));
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                mPublicKey = keyFactory.generatePublic(publicX509);
                cipher = Cipher.getInstance("RSA", "BC");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String decode(String source) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, mPublicKey);
                byte[] output = cipher.doFinal(Base64.decode(source.getBytes()));
                return new String(output, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "53100110852018092109420100000117866950538SDZT|2018-09-21 09:42:01";
//        String pri =
//                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDXhRqf9R1np2cNLabm/3nYqP8DUaqm3DykcX//T0mJ9" +
//                        "/JyzufJ3JdAhZ4eoCoOLPKrSkWy/GMQLZJG3JTs" +
//
// "+PPqypNjVRSoXUbLwiQeSvx7HFosprsorgCucuneo9tzmM719zWI69QhfdJn8uVv7o1L6GqFMAVwdIs4oEn9u8tHlY38oHJWPCC94wU0QXTzB4P9GLddFWy9ub
// /rwSHbPEVNlsRvtbcpU0IRkdNWh6lqbZVPCmynsrkx8y94UUfF4K2u8o7A+9F/grIzeBxo7
// /vXaGAq54xKckj8wGu6ddOyyMs3dsCu4TaI1JPLwzZtfDKE0LNkiqSLee1pnCV5rUplAgMBAAECggEAdbdJ95yWVwtkihopU3qvPtvvjgyJPWKF7Pvhgb342NcJIhRJHkTSiHdzCh9JWTzLvytuLnkUUr3Ra8sHvPetoszK/DJ4eg2YoaqEt9cVWNj/l+vpxipkTOSPQQAW2WjZghdoskS9NR3YUtak4yJVIuTraZv1HJtLf75tK53rRkk1CTFfs3nAzRZjF8ySuxdeiyu9EUaxMrP9LItcuLyiOIVNDF10a3iMXDZ6YeRvsPB/6CJ6dQNO5J7T7bhs5NKnsSvQ5/VqGtFBzHvMJzSwdSKfsrNpl3vgdckSL84p2if3bedNzNIV1VGO6gC0Psyyg1bUkTzdmkJ2NBRh+lK1AQKBgQDuWTGJPFHG4g8QOqUThNxVFUy0fs8epKMRdi7s12dUuSzgHPuYA/58SsarJjJsuv+rclsO+x91ydxsfPDCwnpsw8YBjTIPqUU0T+fqaWQoplCDtHgbQWruSbl1nTuLwE3wTh1seEP9JqAzXeZ2QB2k7paL6UtSVA5iZAtaLRaX0QKBgQDnexwAnBfrVM+ULvbm99ZEbZLL4RRtGPc3FI/+zgqy7KPAcEnOhqTJSinE1eqF9yUEmsl+Z1Wz41eb2CGYV6Yg2hl0xh9j8tKdABnnFbXSYLN5j3GdEZJTR0FgS5/+e7ia9mcP7bm51M6JI7AxuFyghitWMghu+OoRBnZneLlCVQKBgEHjjEsYzq6BLZLZ82pq7xHiOnsnFxCsiJu6JQy6G0mTizFFMdP7Pq2TakpD2+qs4yK9ESkaCL9iFzoya8gbRQauNB2Xr8Aelg/5Cu6FRffqyhUunYlDU1EeNlOqLJzcg28r2Px0Xq8rr1uj2KLV5JwEjL+OtZDDxdozN+Qs1LMRAoGAUXwWyWgpS2AZilJV+8w2kvP3fumGpTjqnWsRnWKuhXmj0zQz5w2iqHEA7xdWbT4ou8w5LdP2aVKYHnYgrV39Twc3etHbbvWlijrk42HTFXGQ/DOwjNgIqGQm8Vztv0mSHIqskQDgJjhxU/lKJtZ9VMymKLuIGHhq/P9HYvkuTdkCgYEAvVBSypejQbOihD6xRJwQyLGDfpJMqI61oveF+lp0PsrwiQC2Crpre6O2w9WIq2SN7dlnz279mNxtqZPqcyBjjcYYRf8LDFnFe2KPnK+AuNcCEa9iIZcBS8QbYZo/p03g4jfHfu0QCNu2Nn4WD6dZrsbFvW8zDz9487baK07YXGg=";

        String prodPri = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCsYqaSVRzJB9QkqleNU/6pIInPOtdNRST8IIJP819pa2jrx8UON+YTm+kdJsEkXfNLZjYtARunPAHojs2HW4YCRfy9g8zHz/4HTRvJ1fmEai2D+GPj03dPfwz4Q6exrXIrvs35TxX6hBECtHV7j2st4NCWNRsW8cKjwJXVe2AESMsML3W/Ff0KkgU9JBneVk1Pnh9M7QTuEYIEHSMkZhkPd7XPa1qhBh88vXrEHHsgtcGOUfNXAUuVYzA2c3v/Ep+JX6vGmS7PeIeyRks83Qu4fPizHzJXwprU0PfBXVRIBXKrv8Ynl26z8ploIdmFdKquUy2ex+DETDLHQ23Z0bHRAgMBAAECggEARRdpAb+BQQWVPnjTjH581BaJhXhk8MBk2XTCJf86X4tASzgPP+qhmXxky11MDiJlr/OwhQsrpTSE40vRDo5dQK4CZYbc3bz8aLq8B4JudmcEBR/aEWchK8iVTy/2l10YJHZeIa1T9stV8gqYB8TsxT+U+o8gY2rUg0NeCmlfi3dkLSU47HyNNxFNy+668B0gYK4bqgWZG7+hEGuBKxEP/kzU0SjGIJs8hHZbF+rS6o7QctXVdWCGcGBHN6YD7yG9BKbBOIkiap9DQEYrlmwWJcSqnp4R3oGX8IgfY+sVVXEo4tL1jzLynM8Btjjh8XFN29I/1tVwWI3PfM1LthBuwQKBgQDpcIljw1ufTN+QxMFqD4T+cC/S99WFKph3h3nOxZ3Bz0PPu2hsWAqRAeCR7iMFT3dPlRicGoFAErtdRXdPkVyipq8XQT1nbDCzbkbCcof1JSvXQTW5DWot/v/61VxQ46ykcWd3heUsYfzegfAtoQG+J0t6sK86luBk15RwFkRi6QKBgQC9C5aSxU0imIe/vyCIHRjv2bmD1cHAe9x7YAcapgEt54jiIY/+G+dNUvTZphfDKw58F+bgXIkhTbU6km+opoieUMsXOP6d3LhAVaTqrxDorEJImhac4x7K0zkqGrFYrziccfUgnm+qf45Cv1ckuxCQTJdycIP2GuHvptV0OU12qQKBgCb/CbSEuhWOffptI/JXzVvD2flgNWcdioLNOufz0zrBKQ7Wu8b9niZDLgSyx17nu4Kr4YSSWO0fM/WM2TWNjvAM8EruNxfD1w7iaSeqglPNddlrp1HWj9pHMvPwGI/auMhvuV0aHQKhC+EPy9OThfsCDXXddihPdATkjoN69MmhAoGBAKiB2NCw/8WTsoUPZ38MeLrrFvrVoaPihDCQ8yQkzq5NUO1IP2KhGrcI9+m9rDayfXHNmGVq9mzyKFWyS8dH0kVT/QORMRwjrzThYmQycl8DlP3EI93qcPsX9j96lZRYc5y302kk8KJImtUSWyoJeIU3UFOYyXeolLYavu+fWFYpAoGBAIi8FJ6/KXAs1GcCLP+AuddR5oam6k5VYnqKWLTmablgiiyRAfsFVCej276eH/4EWM2Nm4erSy8mpMJAECjOUZDUC4eaaO9Jpw++W1ttL7xHw5t7BsSZtvmBNYiGN9+QRjUP3isGUpN/P6hRIBEj2ymvoanMGw6MqZSjvLbOCvb1";
        String testPri = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDXhRqf9R1np2cNLabm/3nYqP8DUaqm3DykcX//T0mJ9/JyzufJ3JdAhZ4eoCoOLPKrSkWy/GMQLZJG3JTs+PPqypNjVRSoXUbLwiQeSvx7HFosprsorgCucuneo9tzmM719zWI69QhfdJn8uVv7o1L6GqFMAVwdIs4oEn9u8tHlY38oHJWPCC94wU0QXTzB4P9GLddFWy9ub/rwSHbPEVNlsRvtbcpU0IRkdNWh6lqbZVPCmynsrkx8y94UUfF4K2u8o7A+9F/grIzeBxo7/vXaGAq54xKckj8wGu6ddOyyMs3dsCu4TaI1JPLwzZtfDKE0LNkiqSLee1pnCV5rUplAgMBAAECggEAdbdJ95yWVwtkihopU3qvPtvvjgyJPWKF7Pvhgb342NcJIhRJHkTSiHdzCh9JWTzLvytuLnkUUr3Ra8sHvPetoszK/DJ4eg2YoaqEt9cVWNj/l+vpxipkTOSPQQAW2WjZghdoskS9NR3YUtak4yJVIuTraZv1HJtLf75tK53rRkk1CTFfs3nAzRZjF8ySuxdeiyu9EUaxMrP9LItcuLyiOIVNDF10a3iMXDZ6YeRvsPB/6CJ6dQNO5J7T7bhs5NKnsSvQ5/VqGtFBzHvMJzSwdSKfsrNpl3vgdckSL84p2if3bedNzNIV1VGO6gC0Psyyg1bUkTzdmkJ2NBRh+lK1AQKBgQDuWTGJPFHG4g8QOqUThNxVFUy0fs8epKMRdi7s12dUuSzgHPuYA/58SsarJjJsuv+rclsO+x91ydxsfPDCwnpsw8YBjTIPqUU0T+fqaWQoplCDtHgbQWruSbl1nTuLwE3wTh1seEP9JqAzXeZ2QB2k7paL6UtSVA5iZAtaLRaX0QKBgQDnexwAnBfrVM+ULvbm99ZEbZLL4RRtGPc3FI/+zgqy7KPAcEnOhqTJSinE1eqF9yUEmsl+Z1Wz41eb2CGYV6Yg2hl0xh9j8tKdABnnFbXSYLN5j3GdEZJTR0FgS5/+e7ia9mcP7bm51M6JI7AxuFyghitWMghu+OoRBnZneLlCVQKBgEHjjEsYzq6BLZLZ82pq7xHiOnsnFxCsiJu6JQy6G0mTizFFMdP7Pq2TakpD2+qs4yK9ESkaCL9iFzoya8gbRQauNB2Xr8Aelg/5Cu6FRffqyhUunYlDU1EeNlOqLJzcg28r2Px0Xq8rr1uj2KLV5JwEjL+OtZDDxdozN+Qs1LMRAoGAUXwWyWgpS2AZilJV+8w2kvP3fumGpTjqnWsRnWKuhXmj0zQz5w2iqHEA7xdWbT4ou8w5LdP2aVKYHnYgrV39Twc3etHbbvWlijrk42HTFXGQ/DOwjNgIqGQm8Vztv0mSHIqskQDgJjhxU/lKJtZ9VMymKLuIGHhq/P9HYvkuTdkCgYEAvVBSypejQbOihD6xRJwQyLGDfpJMqI61oveF+lp0PsrwiQC2Crpre6O2w9WIq2SN7dlnz279mNxtqZPqcyBjjcYYRf8LDFnFe2KPnK+AuNcCEa9iIZcBS8QbYZo/p03g4jfHfu0QCNu2Nn4WD6dZrsbFvW8zDz9487baK07YXGg=";

        System.out.println(prodPri.length());
        String ss = new Encoder(prodPri).encode(s);
        System.out.println(URLEncoder.encode(ss,"UTF-8"));

    }
}
