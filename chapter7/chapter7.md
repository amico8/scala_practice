# 第7章 組み込みの制御構造
* [7.1 if式](#7.1)
* [7.2 whileループ](#7.2)
* [7.3 for式](#7.3)
* [7.4 try式による例外処理](#7.4)
* [7.5 match式](#7.5)
* [7.6 breakとcontinueを使わずに済ませる](#7.6)
* [7.7 変数のスコープ](#7.7)
* [7.8 命令型スタイルのコードのリファクタリング](#7.8)
* [まとめ](#matome)

---

<a name="7.1"></a>
## 7.1 if式
命令型スタイルのif
```scala
var say = "hogehoge"
if (!args.isEmpty)
  say = args(0)
```

関数型に書き換えてみると、こんな感じ↓
```scala
val say = if (!args.isEmpty) args(0) else "hogehoge"
```
ifの結果値は選択された値になり、変数sayはその値で初期化される！

* valを使用しているので、変数が書き換えられてないかをチェックする手間が省ける
* 等式推論をサポートしやすくなる  
式に副作用がなければ、valによって導入される変数は、それを計算する式と等しい  
つまり、変数名の代わりに計算式を書くことができる！

```scala
var say = if (!args.isEmpty) args(0) else "hogehoge"
println(say)

// 上記を1行で書ける
println(if (!args.isEmpty) args(0) else "hogehoge")
```


<a name="7.2"></a>
## 7.2 whileループ
* while と do-while は、これらの処理からは意味のある結果値が得られないため、式ではなくループと呼ばれる
* while と do-while の結果値はUnitである
* Unit型の値は1つで、「()」と書かれる（Unit値）。Javaのvoidとは異なる

```scala
// 出力するだけのUnit型関数。greetはUnit値の()を返す
scala> def greet(){ println("hi") }
greet: ()Unit

// greetの結果値とUnit値の()を比較している
scala> greet() == ()
// あれ、なんか警告がでる・・・
<console>:12: warning: comparing values of types Unit and Unit using '==' will always yield true
              greet() == ()
                      ^
warning: there was one deprecation warning; re-run with -deprecation for details
hi
res8: Boolean = true
```

```scala
var line = ""
while ((line = readLine()) != "") // このような書き方をするとエラーになる
  println("Read: " + line)

// Unit型の値とString型の値を「!=」で比較すると、必ずtrueになるがいいのか？という警告
<console>:9: warning: comparing values of types Unit and String using '!=' will always yield true
              while ((line = readLine()) != "")
                                         ^
warning: there was one deprecation warning; re-run with -deprecation for details
```
* Javaでは、代入の結果値は代入された値で、この場合だと標準入力からの行になる
* Scalaでは、代入の結果値は常にUnit値「()」になり、「""」にはならないため、永久ループになってしまう
* 一般的に、varをさけるのと同じようにwhileもさけた方がよい
* whileループは値を生み出さないので、プログラムに何らかの違いを生むためには通常、varを更新するか入出力処理を実行する必要がある


<a name="7.3"></a>
## 7.3 for式
* Scalaのfor式は、反復処理のスイスアーミーナイフである＝単純な反復処理だけでなく、色々なことができるらしい。

### コレクションの反復処理
forが実行できる最も簡単な処理は、反復によってコレクションのすべての要素を処理すること。
```scala
// カレントディレクトリのすべてのファイルをリストアップする
val filesHere = (new java.io.File(".")).listFiles
for (file <- filesHere)
  println(file)
```
```scala
// この構文をジェネレータと呼ぶ
// 反復処理をするたびに、fileという名前の新しいvalが要素の値によって初期化される
// filesHereの型がArray[File]なので、コンパイラはfileをFile型と推論する
file <- filesHere
```
for式は、配列だけでなく他のコレクションも処理できる！  
for式の「<-」記号の右辺は、適切なシグネチャを持つ決められたメソッド（この場合はforeach）を定義している任意の型の式で良い

```scala
// こう書くのはめんどくさい
for (i <- List(1,2,3,4))
  println("Iteration " + i)

// Range（レンジ）型
for (i <- 1 to 4)
  println("Iteration " + i)

Iteration 1
Iteration 2
Iteration 3
Iteration 4

// Until型（上限値を外す）
for (i <- 1 until 4)
  println("Iteration " + i)

Iteration 1
Iteration 2
Iteration 3

// この書き方だと、添字の参照がずれてしまう可能性があるので、ジェネレータを使う方が安全
for(i <- 0 to filesHere.length - 1)
  println(filesHere(i)) 
```

### フィルタリング
* コレクションのすべての要素を反復処理するのではなく、フィルタをかけて処理対象の要素を抜き出しサブセットしてから処理することもできる
```scala
val filesHere = (new java.io.File(".")).listFiles
for (file <- filesHere if file.getName.endsWith(".txt"))
  println(file)
```

```scala
// ↑と同義の命令型。
// しかし、for式が意味のある値を結果値として返す「式」という定義に基づくと、↑の方が好ましい  
// (結果値は、for式の<-節で型が決まるコレクション)
val filesHere = (new java.io.File(".")).listFiles
for (file <- filesHere)
  if (file.getName.endsWith(".txt"))
    println(file)
```

```scala
// ifなどのフィルタは増やすこともできる
// ディレクトリを除き、ファイルだけを出力する処理
val filesHere = (new java.io.File(".")).listFiles
for (file <- filesHere
  if file.isFile
  if file.getName.endsWith(".txt")
  ) println(file)
```

### 入れ子の反復処理
* 複数の「<-」を追加して、ループを入れ子にすることもできる
```scala
// カレントディレクトリのすべてのファイルをリストアップする
val filesHere = (new java.io.File(".")).listFiles

// ファイルに書いてある全行をListとして格納するメソッド
def fileLines(file: java.io.File) =
  scala.io.Source.fromFile(file).getLines().toList

// grep処理
def grep(pattern: String) =
  for (
    file <- filesHere
      if file.getName.endsWith(".txt"); // 拡張子が「.txt」のファイルのみ処理　「;」をつける！！！
      line <- fileLines(file)
        if line.trim.matches(pattern) // 「.txt」ファイル内で、該当パターンに当てはまる行を出力
  ) println(file + ": " + line.trim)

grep(".*gcd.*")
```
forの()は、{}にしてもよい。そうすると、「;」を省略することができる。


### 変数への中間結果の束縛
* for式の反復処理の中で、繰り返し使用される式は変数としてまとめることができるぽい（**束縛変数**）  
(今回の例では「line.trim」が1回の反復処理に対し2回出てきている)
* 束縛変数は、そのスコープがfor式の中にとどまるのでvalキーワードを省略できる
```scala
def grep(pattern: String) =
  for {
    file <- filesHere
      if file.getName.endsWith(".txt")
      line <- fileLines(file)
        trimmed = line.trim // valを省略した束縛変数
        if trimmed.matches(pattern)
  } println(file + ": " + trimmed)
```


### 新しいコレクションの作成
* for式の今までの例だと、反復的に生成された値は操作後破棄されるが、記憶しておくこともできる
* **yield**キーワードを使う
```scala
val filesHere = (new java.io.File(".")).listFiles

def scalaFiles =
  for {
    file <- filesHere
      if file.getName.endsWith(".scala")
  } yield file

// 出力結果
scala> scalaFiles
res0: Array[java.io.File] = Array(./hoge.scala)
```
for yield式の構文は以下のとおり
```
for ＜節＞ yield ＜本体＞
```
節には反復処理のジェネレータやフィルタ、本体には反復処理で実行される処理を書く
```scala
// こんな風にも書ける
def scalaFiles = for(file <- filesHere if file.getName.endsWith(".scala")) yield file

// これはNG
def scalaFiles = for(file <- filesHere if file.getName.endsWith(".scala")){ yield file }
```
書いたソースをまとめてみる（for式の結果値が今までの例と異なる）
```scala
val filesHere = (new java.io.File(".")).listFiles

def fileLines(file: java.io.File) =
  scala.io.Source.fromFile(file).getLines().toList

val forLineLengths =
  for {
    file <- filesHere  // カレントディレクトリから全ファイル取得（Array[File]型）
    if file.getName.endsWith(".scala")
    line <- fileLines(file)  // 「.scala」のファイルの全行を取得（Iterator[String]型）
    trimmed = line.trim  // 行の両端の空白を削除
    if trimmed.matches(".*for.*")  // 文字列「for」を持つIterator[String]型に変換
  } yield trimmed.length  // Int型で返されるので、for式の結果値はArray[Int]型になる
```


<a name="7.4"></a>
## 7.4 try式による例外処理
* Scalaのthrowは、結果型を持つ式になっている  
例えば↓の例だと、nが偶数でなければhalfが初期化される前に例外が投げられる。throwはNothing型を返す（何も計算しない型）
```scala
val half = if (n % 2 == 0) n / 2 else throw new RuntimeException("n must be even")
```
try〜catch〜finallyの書き方はこんな感じ↓
```scala
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

var file = new FileReader("input.txt")
try {
  // ファイルを使った処理
} catch {
  case ex: FileNotFoundException => println("FileNotFoundException")
  case ex: IOException => println("IOException")
} finally {
  // ファイルをクローズする
  file.close()
}
```
* Javaと違い、Scalaは例外のキャッチやthrow節宣言が必須になっていない
* @throwアノテーションをつければ、throw節を宣言できる
* finally節はファイルのクローズなどのクリーンアップ処理などを実行するもので、本体やcatch節で計算された値を変更してはいけない


<a name="7.5"></a>
## 7.5 match式
* Scalaのmatch式を使えば、switch文と同じように複数の選択肢から1つを選ぶという処理ができる
* breakがいらない
* Javaのcase文と異なり、整数型やenum定数だけでなくあらゆる型を扱うことができる
* Javaのdefaultが「_」で記述される
* match式が結果値を生成する

副作用のあるmatch式
```Scala
val firstArg = if (args.length > 0) args(0) else ""

firstArg match {
  case "salt" => println("pepper")
  case "chips" => println("salsa")
  case "eggs" => println("bacon")
  case _ => println("huh?")
}
```

結果値を生成するmatch式（処理を分けられる）
```Scala
val firstArg = if (args.length > 0) args(0) else ""
// 食品を選択する処理
var friend =
  firstArg match {
    case "salt" => "pepper"
    case "chips" => "salsa"
    case "eggs" => "bacon"
    case _ => "huh?"
  }
// 選択されたものを表示する処理
println(friend)
```


<a name="7.6"></a>
## 7.6 breakとcontinueを使わずに済ませる
* breakやcontinueは、Scalaの関数リテラルと相性が悪いためあまり推奨されない
* continue→if、break→Booleanに置き換える

先頭が「-」ではなく、かつ拡張子が「.scala」のものを探す処理
```java
// Javaの場合
int i = 0;
boolean foundIt = false;
while (i < args.length) {
  if (args[i].startsWith("-")) {
    i++;
    continue;
  }
  if (args[i].endsWith(".scala")) {
    foundIt = true;
    break;
  }
  i++;
}
```
```scala
// Scalaの場合
var i = 0
var foundIt = false
while (i < args.length && !foundIt) {
  if (!args(i).startsWith("-")) { // contonueがifに
    if (args(i).endsWith(".scala"))
      foundIt = true // breakが書き換えに
  }
  i = i + 1
}
```
でもvarを使用していてSclaっぽくないので、ループを再帰関数に書き直します
```scala
// 入力に整数を取り、条件に合致した添字を返す関数
def searchFrom(i: Int): Int =
  if (i >= args.length) - 1
  else if (args(i).startsWith("-")) searchFrom(i + 1)
  else if (args(i).endsWith(".scala")) i
  else searchFrom(i + 1)

val i = searchFrom(0)
```


<a name="7.7"></a>
## 7.7 変数のスコープ
特になし


<a name="7.8"></a>
### 7.8 命令型スタイルのコードのリファクタリング
* 掛け算プログラムを関数型スタイルで書いてみる
    * printやprintlnの副作用をなくす
    * whileやvarの使用をなくす

```scala
// 1段分を返す
def makeRowSeq(row: Int) =
  for (col <- 1 to 10) yield {
    val prod = (row * col).toString
    val padding = " " * (4 - prod.length)
    padding + prod
  }

// 1段分を文字列として返す
def makeRow(row: Int) = makeRowSeq(row).mkString

// 1行に1段分の文字列を収めた表を返す
def multiTable() = {
  val tableSeq =
    for (row <- 1 to 10)
    yield makeRow(row)
  tableSeq.mkString("\n")
}

// 出力
scala> println(multiTable())
```

* makeRowSeqやmakeRowは、ヘルパー関数と呼ぶ
* mkStringは、シーケンス内の文字列を連結して1つの文字列にして返す
* 文字列 * 整数の演算では文字列繰り返しになる


<a name="matome"></a>
## まとめ
* ジェネレータとイテレータってうまく説明できない・・・foreachとも違うのか・・・
* ジェネレータ節はfor式の{}内？？
