// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ParseException.java

package org.apache.torque.engine.sql;


public class ParseException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ParseException(String err)
    {
        super(err);
    }
}
