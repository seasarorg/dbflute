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
	S2Dao = 1.0.35

CSharp
	.Net-2.0
	S2Dao = 1.0.0



# /============================================================================
#                                                             Setup Environment
#                                                             =================
1. Setup JRE

	SunのPageよりJRE-5.0(以上)をDownloadし、Installして下さい。｛必須｝


2. Setup Ant

	Ant-1.5(以上)を以下のSiteよりDownloadし、Installして下さい。｛必須｝

		http://ant.apache.org/

	Downloadして解凍し、環境変数として{ANT_HOME}を追加します。
	さらに、環境変数{Path}に%ANT_HOME%\binを追加します。


3. Setup Environment-Variable

	環境変数として{DBFLUTE_HOME}を追加します。｛必須｝

		ex) DBFLUTE_HOME = C:\java\dbflute-1.0


4. Locate build-properties and batch-files

	4-1. %DBFLUTE_HOME%/etc/sample4useのDirectory(中のFileも含む)をCopyし、LocalPCの任意の場所に配置して下さい。
	     一般にbuild-propertiesやbatch-filesは各ProjectのVersion管理に含めたいので、それを考慮して配置します。

		※“sample4use”内のFileは、DBFluteのModule(本体)とは別のものです。
		  ここには、DBFluteを実行するための“ちょっとしたBatch”と各ProjectにCustomizeしたPropertyが格納されます。

	4-2. _project.batの「MY_PROJECT_NAME」変数の値を任意のProject名に変更して下さい。
	4-3. build-ldb.propertiesのFile名をbuild-[xxx].properties(xxxはProject名)に変更して下さい。


5. Modify build-properties

	各Propertyの詳細はSampleのbuild-ldb.properties内に記述されているCommentを参考にして下さい。
	NotRequiredのPropertyはDefault値で事足りるならば消しても構いません。

	実行するために最低限考慮する必要があるPropertyを以下に記載します。

	- {Project Basic Property}
		-- torque.project (Required)
			--- Projectの名称。

		-- torque.database (Required)
			--- DBの種類。

		-- torque.java.dir (Required)
			--- Sourceの出力先Directory。
			--- 絶対Path、もしくは、実行Directoryからの相対Path。

		-- torque.java.location.for.gen (NotRequired)
			--- Sourceの出力先の後に付けるGenとして生成するDirectory。
			--- Allcommon(一部例外)とConditionBeanがこのDirectoryに出力されます。
			--- GenとMainでProjectを分けたい場合に利用します(MainのCompileを早くさせるため)。

		-- torque.java.location.for.main (NotRequired)
			--- Sourceの出力先の後に付けるMainとして生成するDirectory。
			--- Allcommon(一部例外)とConditionBeanがこのDirectoryに出力されます。
			--- GenとMainでProjectを分けたい場合に利用します(MainのCompileを早くさせるため)。

	- {OM Task Property}
		-- torque.targetLanguage (NotRequired - Default 'java')
			--- 生成されるSourceの言語。{java or csharp}

		-- torque.isJavaNameOfTableSameAsDbName (NotRequired - Default 'false')
			--- DB上のTableNameがClassで利用したいName(ClassName)と同じか否か。

		-- torque.isJavaNameOfColumnSameAsDbName (NotRequired - Default 'false')
			--- DB上のColumnNameがClassで利用したいName(PropertyName)と同じか否か。

		-- torque.isAvailableEntityLazyLoad (NotRequired - Default 'false')
			--- Lazyloadを有効にするか否か。
			--- trueの時は、Instance化していない関連EntityをGetしたときに内部でSQLを発行してInstanceが取得します。
			--- ☆trueの時は、Application起動時にLazyLoadContainer-Classを初期化する必要があります。
			--- ☆非推奨です。将来的に削除される可能性があります。

		-- torque.isJavaNameOfColumnSameAsDbName (NotRequired - Default 'false')
			--- DB上のColumnNameがClassで利用したいName(PropertyName)と同じか否か。

		-- torque.isAvailableGenerics (NotRequired - Default 'true')
			--- Java-1.4.Xを利用する場合は、falseを指定する必要があります。
			--- CSharpではこのPropertyによる挙動の変化はありません。

		-- {ClassのPackage}
			--- torque.baseCommonPackage (Required)
			--- torque.baseDaoPackage (Required)
			--- torque.baseEntityPackage (Required)
			--- torque.conditionBeanPackage (Required)
			--- ...

		-- torque.identityDefinitionMap (NotRequired - Default 'map:{}')
			--- ID-Annotationを付けるべき{Table=ColumnName}を指定します。(identity列など)

		-- torque.updateDateFieldName (NotRequired - Default null)
			--- Timestamp-Annotationを付けるべきColumnNameを指定します。(更新日時など)
			--- 指定したColumnが定義されているEntity全てにAnnotationが付与されます。

		-- torque.versionNoFieldName (NotRequired - Default null)
			--- VersionNo-Annotationを付けるべきColumnNameを指定します。(VersionNo列など)
			--- 指定したColumnが定義されているEntity全てにAnnotationが付与されます。

	- {JDBC Task Property}
		-- torque.database.driver (Required)
			--- JDBC-DriverのClassFullName。

		-- torque.database.url (Required)
			--- JDBC接続のConnectionURL。

		-- torque.database.host (Required)
			--- JDBC接続の接続先Host。

		-- torque.database.schema (Required)
			--- JDBC接続の接続先Schema。
			--- Oracle/DB2/Derbyは、Schema名は大文字である必要がありますのでご注意下さい。

		-- torque.database.user (Required)
			--- JDBC接続の接続User。

		-- torque.database.password (Required)
			--- JDBC接続の接続Password。

	_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
	※j2ee.diconの設定にて、Component定義を以下のように差し替えてください。
	  これにより、Paging検索周りの機能が有効になります。
	  (CSharp版は'FetchNarrowingResultSetFactory'のみ)

	<component class="org.seasar.extension.jdbc.impl.BasicStatementFactory"/>
	<component class="org.seasar.extension.jdbc.impl.BasicResultSetFactory"/>

		↓↓↓

	<component class="xxx.allcommon.s2dao.LdS2DaoStatementFactory"/>
	<component class="xxx.allcommon.s2dao.LdFetchNarrowingResultSetFactory"/>
	
	<!-- <component class="org.seasar.extension.jdbc.impl.BasicStatementFactory"/> -->
	<!-- <component class="org.seasar.extension.jdbc.impl.BasicResultSetFactory"/> -->

	_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/


6. Execute JDBC and Generate

	6-1. jdbc.batを実行します。./schema以下にproject-schema-xxx.xmlが作成されていたら成功です。
			→中はDBのSchema情報がXML形式で記述されています。

	6-2. generate.batを実行します。Propertyにて指定した出力DirectoryにSourceが作成されていたら成功です。

	※実行するJavaのVersionに注意してください。
	  例えば、DBFluteが“Java-5.0”でCompileされていて、実行がJDK-1.4の場合はExceptionになります。
	  InstallしているJavaのVersionやJAVA_HOMEに指定しているDirectoryなどをご確認下さい。

6. Confirm the behavior

	6-1. 確認のために生成されたSourceをCompileしてみて下さい。

		※Buildに必要なModuleは、後述のDependenciesを参考。

	6-2. 確認のためにCompileされたDaoでSelectしてみて下さい。

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


7. Modify build-properties again

	Projectの要件に合わせて、最適なPropertyを設定していきます。


# /============================================================================
#                                                                   Torque Task
#                                                                   ===========

A. JDBC-Task
	概要：
		DatabaseからJDBC経由でSchema情報を取得する。
		DaoGenを使う上で必須。

B. OM-Task
	概要：
		Schema情報とVelocity-Templateを利用して、S2Dao-Objectを生成する。
		Velocity-Templateは'%DBFLUTE_HOME%/templates/om'以下に存在する。

C. Document-Task
	概要：
		Schema情報を利用して、Schema情報のHTML-Documentを生成する。
		Batchを実行したCurrentDirectoryの'output/doc'以下に生成される。

D. Invoke_ReplaceSchema-Task
	概要：
		指定されたSqlFile(DropTableとCreateTableのDDL文)を実行してDBのSchemaを作り直す。
		複数のSQL文を区切るDelimiterは';'です。

E. Invoke_SqlDirectory-Task
	概要：
		指定されたDirectory以下(再帰的)のSqlFileを全て実行する。
		拡張子は'.sql'です(大文字小文字区別なし)。
		複数のSQL文を区切るDelimiterは';'です。
		S2Daoの“Test値を利用した外だしSQL”の一斉実行が可能。


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

