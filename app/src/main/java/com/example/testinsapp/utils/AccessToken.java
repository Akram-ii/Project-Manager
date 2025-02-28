package com.example.testinsapp.utils;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AccessToken {
    private static final String FIREBASE_MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    public String getAccessToken(){
        try{
            String jsonString="{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"project-manager-appli\",\n" +
                    "  \"private_key_id\": \"6d4aabe96e022fecf1c834c66eb2db24b2e62868\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDENjYFYJQoz5U0\\nB/oeisgHika4mjvfIJ/n4bq/gnlPkV2jJWDus4gKnMwV3WyYemyrUSY+0jp+XwuP\\nqDg+cVuEY1Yx2mDmruz9v/W0qszmuqyIFJ9XEKHXDh2UAQrEdBAfWy4Q4IVWcMJ7\\n0yRuzRPxo4XQOjK+yzkQ+aTWOxlZ6AFpYAkro/pK/rKrJWL8WnSNQliC0u9Bi72r\\nZ5INFwTIFU22MExPZksZ1wA9U6ZZV2nKkCM1X8Qj38hgJn0jMuIct51DKm3N2hYT\\n2GBWf5vPHQjBhgKOrcaI2rMADyzMQkZ9FB9DfIBr//f7fMLPe0kPh5oB/3YXU+rU\\njZlFdsP3AgMBAAECggEAR1Cc2d98ekjYpgywMCGpCnSLt9T3JeYpuJIm2iyGAxGG\\nwyF21cuTCqhq1RehvNniXXIlQHgQopXREenGZ4u0A65HSd9hkPbLVH9MwtnBl6sJ\\nwnwcXfzBlfSfc4fdbgHxSjRNzfyM+hoJ1g+sEkmfkhgpLsG4Z/hdSXozG1pnoHE7\\nd+ZaQxcWoyDHzXYNYpM+Pjd4KCy2opT7iLpDNXAruoS/MAezBRtz5ivMi7/OyM9z\\nRdu73XT1vJx0NO4MSoYp9LXm65NclgqKy9jItsEgloVlNfChCS8/SjfmZQzTjuRy\\nYLirY3y9U2zqsCi+49gza6Vb+2b0SaaIwdkcV3f6YQKBgQDzcYjKe/FBd2R1lezK\\nfkt+ukzT6H9n1nP8mc5Af4HA7XZnFJuMsT/27stMCu9LvmGfJigtQ52MQ7/JMsjV\\nLzF6nTBiG5c6HHdcmsyB7D2/YoB6aSJ86br9opMv9ymEe0/QFvq4AY6hchb/wTdR\\nfuM0HvO34ENJpVQyVHQoxWxUmwKBgQDOVQWFWElsUz5L49mEJt6suhfMOfp36Cbz\\nFvQGtlfoNyDLqy34yNqzPhfXMd9Rhyk/pPnC3ExZ0epAa3X5ik/arZIyf3aRx+vZ\\nxmNjeKOKD5rW3//iZTFJKcfp5b2KySUBBqx3w381VuoQjEHtgQJx1z+r9RSxBGOv\\n/ARO62GN1QKBgQDSDoQO25VXfNKlF2GDdChmSBBkxPeyPHxGWNx8EMmAFCYXWdPC\\nOWtNAc1PZxwGClcnwP/n1rEJx6ejzvs14Avcleear930WX2w3S1baeH50t+zQCZv\\nDCq6Ed7ZA3bChrhegIWD4mwU6GHp+ullm4wmfBKl9Y9563tUFkVIrNkFwwKBgD39\\nmV0Iu3aKRAs096MWsowelQvM142y+BDM8qAZpKul9Hp8gaFfZyW3IB6NY1TxUNlI\\nTanX2jCaOtED+Dvy7C79OxFk7lC6Sgfx7OZz1l6idZjdT9nLIVWj8eY6GgpaiH2Y\\nbbTFzbBO3p/+I8ihnARr8TtOtNrCQNkc1IaJfZPJAoGBAKtEFk3x8Km3vCYTMvT4\\ny9VUKSv4WHc+4rUlYw6/xS+78O7cT/vLrxIx4s5aMxs199iv/uUj490P17FkhN8c\\nRj+LkLXSU07m+k35CIhh5TXaEVZsd55f1b013HvNMmv4KfJCMnL1Tsz4lRLi61Ko\\nWt83vUlMl6Yr4nh/SPfkY0PO\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-avdts@project-manager-appli.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"104796325834808032146\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-avdts%40project-manager-appli.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream=new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials=GoogleCredentials.fromStream(stream).createScoped(Lists.newArrayList(FIREBASE_MESSAGING_SCOPE));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            Log.e("error", "getAccessToken: "+e.getMessage() );
            return null;
        }
}
}
