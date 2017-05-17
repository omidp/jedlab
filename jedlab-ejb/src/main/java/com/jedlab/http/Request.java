package com.jedlab.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import javax.net.ssl.HttpsURLConnection;

public class Request
{

    public static final int DEFAULT_TIMEOUT = 4000;

    private String url;

    /**
     * request header info
     */
    private Map<String, String> headers;

    /**
     * request form data
     */
    private Map<String, Object> formdatas;

    private HttpMethodRequest httpMethod;

    public Request(String url, HttpMethodRequest httpMethod)
    {
        this.url = url;
        this.httpMethod = httpMethod;
        this.formdatas = new WeakHashMap<>();
        this.headers = new WeakHashMap<>();
    }

    public Response execute()
    {
        try
        {
            URL _url = new URL(this.url);
            final HttpURLConnection urlConn = url.startsWith("https") ? (HttpsURLConnection) _url.openConnection()
                    : (HttpURLConnection) _url.openConnection();
            urlConn.setRequestMethod(httpMethod.name());
            urlConn.setReadTimeout(DEFAULT_TIMEOUT);

            // Setting header
            Iterator<Map.Entry<String, String>> its = headers.entrySet().iterator();
            while (its.hasNext())
            {
                Map.Entry<String, String> entry = its.next();
                urlConn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // Send post data
            if (this.httpMethod == HttpMethodRequest.POST)
            {

                String urlParameters = this.postParams();
                if (null != urlParameters && !urlParameters.equals(""))
                {
                    urlConn.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(urlConn.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();
                }
            }
            return new Response(urlConn.getResponseCode(), urlConn.getContentLength(), urlConn.getDate(), urlConn.getResponseMessage(),
                    urlConn.getContentType(), urlConn.getInputStream());
        }
        catch (MalformedURLException e)
        {

        }
        catch (IOException e)
        {

        }
        return new Response(500, 0, 0, "", "", null);
    }

    private String postParams()
    {
        if (formdatas.size() > 0)
        {
            // url has been a parameter
            // e.g:sn=C02G8416DRJM&cn=&locale=&caller=&num=12345
            StringBuffer sb = new StringBuffer("");
            Iterator<Map.Entry<String, Object>> its = formdatas.entrySet().iterator();
            while (its.hasNext())
            {
                Map.Entry<String, Object> entry = its.next();
                sb.append('&').append(entry.getKey()).append('=').append(entry.getValue());
            }
            return sb.substring(1);
        }
        return null;
    }

}
