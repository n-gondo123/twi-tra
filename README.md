概要
=================================
TwitterをScalaのPlay2で作ってみた

起動方法
=================================
MySQLの設定(ユーザー名: root, パスワードは空文字列)  
```
# in root
$ mysql -u root -p

mysql> create database twi_tra
mysql> use twi_tra
mysql> source sql/create.sql
```

slickでモデルを生成  
```
# in root
$ cd slick-codegen
$ ./sbt.sh slick-codegen
```

実行
```
# in root
./activator

[twi_tra] $ run
```
