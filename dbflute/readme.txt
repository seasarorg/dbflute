#
# **************
# DBFlute-readme
# **************
# Written by jflute (Last updated at 2006/09/08 Tue.)
#
# ※このテキストは4タブで参照して下さい。
# ※ルーラーの最大を120以上に設定して下さい。

# /============================================================================
#                                                               Support Version
#                                                               ===============
Java
	JDK-1.4.X or JDK-5.0
	S2Dao = 1.0.36 (35-OK)

CSharp
	.Net-2.0
	S2Dao = 1.0.0



# /============================================================================
#                                                             Setup Environment
#                                                             =================

# -------------------------------------------
#                                  Java & Ant
#                                  ----------
1. Setup JRE

    SunのPageよりJRE-5.0(以上)をDownloadし、Installして下さい。｛必須｝


2. Setup Ant

    Ant-1.5(以上)を以下のSiteよりDownloadし、Installして下さい。｛必須｝

        Apache Ant

    Downloadして解凍し、環境変数として{ANT_HOME}を追加します。
    さらに、環境変数{Path}に%ANT_HOME%\binを追加します。


# -------------------------------------------
#                        Environment-Variable
#                        --------------------
3. Setup environment-variable

    環境変数として{DBFLUTE_HOME}を追加します。｛必須｝

        ex) DBFLUTE_HOME = C:\java\dbflute-1.0


# -------------------------------------------
#                            Client-Directory
#                            ----------------
4. Locate client-directory(containing build-properties and batch-files)

    4-1. %DBFLUTE_HOME%/etc/client_directory-templateのDirectoryに、実行Directory(Client-Directory)の
         Templateがあります。任意のTemplateをCopyし、LocalPCの任意の場所に配置して下さい。

        ※一般にbuild-propertiesやbatch-filesは各ProjectのVersion管理に含めたいので、それを考慮して配置します。
        ※Templateは、以下のようになっています。

            - ldb_fullProperties:
                全てのPropertyが記述されています。
                DBFluteを使い慣れていて、最初からFULL機能をじっくり考えるために
                一個一個のPropertyを精査していく場合は、これをお奨めします。

            - ldb_minimumProperties:
                最低限のPropertyが記述されています。
                とりあえず自動生成をしてみてその後徐々に機能を追加していく場合は、
                これをお奨めします。｛DBFlute初心者の方はこれ｝
                (準備されているAnt-Taskはfullと同様です)

            - ldb_schemaHTMLOnly:
                HtmlDocument-Taskを実行するための最低限のPropertyだけが記述されています。
                DBFlute初心者でとりあえずSchemaHTMLだけ利用してみたいという方、
                また、他のO/R-Mapperを使っているがSchemaHTMLだけを利用してみたいという方はこれを利用して下さい。
                (DBの種類やDBの接続先などを設定するだけですぐに利用可能)

    4-2. _project.batの「MY_PROJECT_NAME」変数の値を任意のProject名に変更して下さい。

    4-3. build-ldb.propertiesのFile名をbuild-[xxx].properties(xxxは任意のProject名)に変更して下さい。

    ※schemaHTMLOnlyを利用する場合は、特にProject名にこだわる必要はありません。(そのままでもOK)


# -------------------------------------------
#                     Properties Modification
#                     -----------------------
5. Modify build-properties

    各Propertyの詳細は%DBFLUTE_HOME%/etc/client_directory-template/ldb_fullPropertiesの
    build-ldb.properties内に記述されている各PropertyのCommentを参考にして下さい。
    (NotRequiredのPropertyはDefault値で事足りるならば消しても構いません)

    _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    j2ee.diconの設定にて、Component定義を以下のように差し替えてください。
    これにより、Paging検索周りの機能が有効になります。
    (CSharp版は'FetchNarrowingResultSetFactory'のみ)

    component class="org.seasar.extension.jdbc.impl.BasicStatementFactory"
    component class="org.seasar.extension.jdbc.impl.BasicResultSetFactory"

        ↓↓↓

    component class="xxx.allcommon.s2dao.S2DaoStatementFactory"
    component class="xxx.allcommon.s2dao.FetchNarrowingResultSetFactory"

      ※注意：j2ee.diconは自動生成対象外です。(dao.diconは自動生成)
    _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/


# -------------------------------------------
#                              Task Executing
#                              --------------
6. Execute task of JDBC and Generate

    6-1. jdbc.batを実行します。./schema以下にproject-schema-xxx.xmlが作成されていたら成功です。
            →中はDBのSchema情報がXML形式で記述されています。

    6-2. generate.batを実行します。Propertyにて指定した出力DirectoryにSourceが作成されていたら成功です。

    ※実行するJavaのVersionに注意してください。
      例えば、DBFluteが“Java-5.0”でCompileされていて、実行がJDK-1.4の場合はExceptionになります。
      InstallしているJavaのVersionやJAVA_HOMEに指定しているDirectoryなどをご確認下さい。


# -------------------------------------------
#                                Confirmation
#                                ------------
7. Confirm the behavior

    7-1. 確認のために生成されたSourceをCompileしてみて下さい。

    7-2. 確認のためにCompileされたDaoでSelectしてみて下さい。

        ex) BOOKというTableが存在している場合
        ｛PrimaryKeyによる一意検索｝
        /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        final BookDao dao = (BookDao)container.getComponent(BookDao.class);
        Book entity = dao.getEntity(new BigDecimal(4));
        - - - - - - /

        ｛ConditionBeanによる一意検索｝
        /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        final BookCB cb = new BookCB();
        cb.query().setBookId_Equal(4);
        final BookDao dao = (BookDao)container.getComponent(BookDao.class);
        Book entity = dao.selectEntity(cb);
        - - - - - - /

# -------------------------------------------
#                          Modification again
#                          ------------------

8. Modify build-properties again

    Projectの要件に合わせて、最適なPropertyを設定していきます。






# /============================================================================
#                                                                   Torque Task
#                                                                   ===========

A. JDBC-Task
    概要：
        DatabaseからJDBC経由でSchema情報を取得します。

    詳細：
        実行すると'./schema'以下に取得したSchema情報がXMLとして保存されます。
        まずこれを実行しなければ始まりません。



B. OM-Task
    概要：
        Schema情報とVelocity-Templateを利用して、S2DaoのClassを生成します。

    詳細：
        JDBC-Taskにて取得したSchema情報とbuild.propertiesを元に、Velocity-Templateを
        利用してClassを自動生成します。DBFluteのMainの機能です。
        Velocity-Templateは'%DBFLUTE_HOME%/templates/om'以下に存在します。



C. SchemaHTML-Task
    概要：
        Schema情報を利用して、Schema情報のHTML-Documentを生成します。(SchemaHTML)

    詳細：
        Batchを実行したCurrentDirectoryの'./output/doc'以下に生成されます。




D. Invoke_ReplaceSchema-Task
    概要：
        指定されたSqlFile(DropTableとCreateTableのDDL文)を実行してDBのSchemaを作り直す。

    詳細：
        複数のSQL文を区切るDelimiterは';'です。

        単にSQL文を実行しているだけなので、別にDDLでなくても実行されます。
        一応、明示的に'DBのReplace用'と銘打っているだけです。
        DBをReplaceすると同時に、Master-TableのRecordを格納したり、
        Test用のRecordを登録したりするのにも利用できます。

        build.propertiesでは、JDBC-Taskで利用するDB接続先情報と
        'invokeReplaceSchemaDefinitionMap'が必要となります。



E. Invoke_SqlDirectory-Task
    概要：
        指定されたDirectory以下(再帰的)のSqlFileを全て実行する。

    詳細：
        拡張子は'.sql'です(大文字小文字区別なし)。
        複数のSQL文を区切るDelimiterは';'です。

        - - - - - - - - - - - - - - - - - - - - - - - - - - 
        S2Daoの“Test値を利用した外だしSQL”の一斉実行が可能
        - - - - - - - - - - - - - - - - - - - - - - - - - - 

        build.propertiesでは、JDBC-Taskで利用するDB接続先情報と
        'invokeSqlDirectoryDefinitionMap'が必要となります。





# /============================================================================
#                                                                  Dependencies
#                                                                  ============
Java
	Seasar-2.3.10
	S2Dao-1.0.35
	Commons-Logging-1.0.3 over
	log4j-1.2.8.jar over
	Aopalliance-1.0
	Javassist-3.0
	Ognl-2.6.5

CSharp
	Seasar-1.2.4
	S2Dao-1.0.0


# /============================================================================
#                                                                  Supported DB
#                                                                  ============

- DBFlute supports all things Torque supports.
	/-----------------------------------------------------------
	axion, cloudscape, db2, db2400, hypersonic, interbase, mssql
	, mysql, oracle, postgresql, sapdb, sybase, firebird, derby
	--------------------/

# /============================================================================
#                                                                   Restriction
#                                                                   ===========

- DBFlute does not support that Changing xxxPrefixes(ex.insertPrefixes). Sorry!
  Please use default name. {update, insert, modify, remove...}

