package com.jedlab.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author Omid Pourhadi
 *
 */
public class ByteUtil
{

    
    /**
     * useful to inject into jbpm context
     * 
     * @param serializableObject
     * @return
     */
    public static byte[] convertObjectToByte(Object serializableObject)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        byte[] result = null;
        try
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serializableObject);
            result = baos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (baos != null)
                {
                    baos.flush();
                    baos.close();
                }
                //
                if (oos != null)
                {
                    oos.flush();
                    oos.close();
                }
            }
            catch (IOException e)
            {
                // ignore close quietly
            }

        }

        return result;
    }

    public static Object convertByteToObject(byte[] b)
    {
        ByteArrayInputStream bias = new ByteArrayInputStream(b);
        ObjectInputStream i = null;
        Object result = null;
        try
        {
            i = new ObjectInputStream(bias);
            result = i.readObject();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(bias);
            IOUtils.closeQuietly(i);
        }
        return result;
    }
    
}
