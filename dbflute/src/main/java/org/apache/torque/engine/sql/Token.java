// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Token.java

package org.apache.torque.engine.sql;


public class Token
{

    public Token(String str)
    {
        this(str, 0, 0);
    }

    public Token(String str, int line, int col)
    {
        this.str = str;
        this.line = line;
        this.col = col;
    }

    public String getStr()
    {
        return str;
    }

    public int getLine()
    {
        return line;
    }

    public int getCol()
    {
        return col;
    }

    public String toString()
    {
        return str;
    }

    private String str;
    private int line;
    private int col;
}
