package org.jedlab.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.SQLException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.google.common.io.Files;
import com.jedlab.payment.SSLUtilities;

public class CompanyCrawler extends DBConnection
{

    @Test
    public void test() throws SQLException, IOException, KeyManagementException, NoSuchAlgorithmException
    {
        

        try
        {
            JSONArray comp= new JSONArray(); 
            int index = 0;
            boolean exists = true;
            do
            {
                JSONArray data = getData(index);
                index +=36;
                for (int i = 0; i < data.length(); i++)
                {
                    JSONObject jsonObject = data.getJSONObject(i);
                    comp.put(jsonObject.toMap());
                }
                exists = data.length() > 0;
//                exists = index < 40;
                
            } while (exists);
//            System.out.println(comp.toString());
//          Files.write(comp.toString().getBytes(),new File("/home/omidp/jedlabProject/jedlab/source/jedlab-tests/src/test/resources/companyList.json"));
            fastCopy(IOUtils.toInputStream(comp.toString()), new FileOutputStream(new File("/home/omidp/jedlabProject/jedlab/source/jedlab-tests/src/test/resources/companyList.json")));
        }
        finally
        {
//            response.close();
        }
        // String string =
        // IOUtils.toString(getClass().getResourceAsStream("/companyList.json"));
        // JSONObject json = new JSONObject(string);
        // JSONArray jsonArray = json.getJSONArray("data");
        // System.out.println(jsonArray.length());
    }
    
    public static void fastCopy(final InputStream src, final OutputStream dest) throws IOException {
        final ReadableByteChannel inputChannel = Channels.newChannel(src);
        final WritableByteChannel outputChannel = Channels.newChannel(dest);
        fastCopy(inputChannel, outputChannel);
    }
    
    public static void fastCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        
        while(src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        
        buffer.flip();
        
        while(buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
    
    private JSONArray getData(int index) throws NoSuchAlgorithmException, KeyManagementException, ClientProtocolException, IOException
    {
        System.out.println("index : " + index);
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();

        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers()
            {
                System.out.println("getAcceptedIssuers =============");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType)
            {
                System.out.println("checkClientTrusted =============");
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType)
            {
                System.out.println("checkServerTrusted =============");
            }
        } }, new SecureRandom());

        SSLSocketFactory sf = new SSLSocketFactory(sslContext);
        Scheme httpsScheme = new Scheme("https", 443, sf);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        // apache HttpClient version >4.2 should use
        // BasicClientConnectionManager
        ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(cm);

        HttpGet httpGet = new HttpGet("https://api.jobguy.ir/public/company/list/?size=50&index="+index+"&industry=%DA%A9%D8%A7%D9%85%D9%BE%DB%8C%D9%88%D8%AA%D8%B1-%D9%81%D9%86%D8%A7%D9%88%D8%B1%DB%8C-%D8%A7%D8%B7%D9%84%D8%A7%D8%B9%D8%A7%D8%AA-%D9%88-%D8%A7%DB%8C%D9%86%D8%AA%D8%B1%D9%86%D8%AA&order_by=HOTTEST");
        httpGet.setHeader("Content-Type", "application/json; charset=utf-8");
        
        HttpResponse response = httpClient.execute(httpGet);
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();
        String string = EntityUtils.toString(entity, Charset.forName("UTF-8"));
        JSONObject json = new JSONObject(string);
        JSONArray jsonArray = json.getJSONArray("data");
        return jsonArray;
    }
    
    
    
    private byte[] downloadImage(String logo) throws NoSuchAlgorithmException, KeyManagementException, ClientProtocolException, IOException
    {
        String uri = "https://media.jobguy.ir"+logo;
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();

        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers()
            {
                System.out.println("getAcceptedIssuers =============");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType)
            {
                System.out.println("checkClientTrusted =============");
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType)
            {
                System.out.println("checkServerTrusted =============");
            }
        } }, new SecureRandom());

        SSLSocketFactory sf = new SSLSocketFactory(sslContext);
        Scheme httpsScheme = new Scheme("https", 443, sf);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        // apache HttpClient version >4.2 should use
        // BasicClientConnectionManager
        ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(cm);
        HttpGet httpGet = new HttpGet(uri);
        
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            long len = entity.getContentLength();
            InputStream inputStream = entity.getContent();
            // How do I write it?
            return IOUtils.toByteArray(inputStream);
        }
        return null;
    }
    

}
