// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SQLScanner.java

package org.apache.torque.engine.sql;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package org.apache.torque.engine.sql:
//            Token

public class SQLScanner
{

    public SQLScanner()
    {
        this(null);
    }

    public SQLScanner(Reader input)
    {
        setInput(input);
    }

    public void setInput(Reader input)
    {
        in = input;
    }

    private void readChar()
        throws IOException
    {
        boolean wasLine = (char)chr == '\r';
        chr = in.read();
        if((char)chr == '\n' || (char)chr == '\r' || (char)chr == '\f')
        {
            col = 0;
            if(!wasLine || (char)chr != '\n')
                line++;
        } else
        {
            col++;
        }
    }

    private void scanIdentifier()
        throws IOException
    {
        token = "";
        for(char c = (char)chr; chr != -1 && "\f\r\t\n ".indexOf(c) == -1 && ";(),'".indexOf(c) == -1; c = (char)chr)
        {
            token = token + (char)chr;
            readChar();
        }

        int start = col - token.length();
        tokens.add(new Token(token, line, start));
    }

    private void scanNegativeIdentifier()
        throws IOException
    {
        token = "-";
        for(char c = (char)chr; chr != -1 && "\f\r\t\n ".indexOf(c) == -1 && ";(),'".indexOf(c) == -1; c = (char)chr)
        {
            token = token + (char)chr;
            readChar();
        }

        int start = col - token.length();
        tokens.add(new Token(token, line, start));
    }

    public List scan()
        throws IOException
    {
        line = 1;
        col = 0;
        boolean inComment = false;
        boolean inCommentSlashStar = false;
        boolean inCommentDash = false;
        tokens = new ArrayList();
        readChar();
        while(chr != -1) 
        {
            char c = (char)chr;
            boolean inNegative = false;
            if(c == '-')
            {
                readChar();
                if((char)chr == '-')
                {
                    inCommentDash = true;
                } else
                {
                    inNegative = true;
                    c = (char)chr;
                }
            }
            if(inCommentDash)
            {
                if(c == '\n' || c == '\r')
                    inCommentDash = false;
                readChar();
            } else
            if(c == '#')
            {
                inComment = true;
                readChar();
            } else
            if(c == '/')
            {
                readChar();
                if((char)chr == '*')
                    inCommentSlashStar = true;
            } else
            if(inComment || inCommentSlashStar)
            {
                if(c == '*')
                {
                    readChar();
                    if((char)chr == '/')
                        inCommentSlashStar = false;
                } else
                if(c == '\n' || c == '\r')
                    inComment = false;
                readChar();
            } else
            if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".indexOf(c) >= 0)
            {
                if(inNegative)
                    scanNegativeIdentifier();
                else
                    scanIdentifier();
            } else
            if(";(),'".indexOf(c) >= 0)
            {
                tokens.add(new Token("" + c, line, col));
                readChar();
            } else
            {
                readChar();
            }
        }
        return tokens;
    }

    private static final String WHITE = "\f\r\t\n ";
    private static final String ALFA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMER = "0123456789";
    private static final String ALFANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SPECIAL = ";(),'";
    private static final char COMMENT_POUND = 35;
    private static final char COMMENT_SLASH = 47;
    private static final char COMMENT_STAR = 42;
    private static final char COMMENT_DASH = 45;
    private Reader in;
    private int chr;
    private String token;
    private List tokens;
    private int line;
    private int col;
}
