# 第3章 Scalaプログラミングの第一歩
* [3.1 [ステップ7] 配列を型でパラメータ化する](#3.1)
* [3.2 [ステップ8] リストを使う](#3.2)
* [3.3 [ステップ9] タプルを使う](#3.3)
* [3.4 [ステップ10] 集合とマップを使う](#3.4)
* [3.5 ](#3.5)
* [3.6 ](#3.6)
* [まとめ](#matome)

---

<a name="3.1"></a>
## 3.1 [ステップ7] 配列を型でパラメータ化する
greetStringsを、String型の長さ3の配列で初期化してみる。  
ただしこの書き方は冗長なので推奨していない。

```scala
val greetStrings: Array[String] = new Array[String](3)
greetStrings(0) = "Hello"
greetStrings(1) = ", "
greetStrings(2) = "world!\n"
for (i <- 0 to 2)
  print(greetStrings(i))
```

greetStringsの初期化は以下のようにも書ける。

```scala
val greetStrings = new Array[String](3)
```

valで変数を定義した時、その変数に再代入することはできないが、変数が参照するオブジェクトは変わる可能性がある。  
greetStringsは、初期化した時と同じArray[String]インスタンスを常に参照するが、後でArray[String]の要素を変更できるので、配列自体はミュータブル（変更可能）である。↓  

```scala
greetStrings(0) = "Hello"
greetStrings(1) = ", "
greetStrings(2) = "world!\n"
```

更に、0 to 2 というメソッドは、(0).to(2)というメソッド呼び出しに変換される。  
Scalaではすべての演算がメソッド呼び出しである。

```scala
for (i <- 0 to 2)
  print(greetStrings(i))
```

配列要素の参照と更新は、コンパイル時、applyメソッド／updateメソッドに変換される。

```scala
// 要素の参照
greetStrings(i)
greetStrings.apply(i)

// 要素の更新
greetStrings(0) = "Hello"
greetStrings.update(0, "Hello")
```

Scala推奨の配列の作成と初期化の書き方はこう↓
```scala
val numNames = Array("Hello", ", ", "world!\n")
```


<a name="3.2"></a>
## 3.2 [ステップ8] リストを使う
* メソッドは副作用を持ってはならない、というのが、関数型プログラミングの考え方の大きな特徴である。  
メソッドの作用は、計算をして値を返すことだけでなければならない。  
* Array（配列）はミュータブルだが、Listはイミュータブルである。

リストの作成・初期化

```scala
val oneTwoThree = List(1, 2, 3)
```

2つのリストを連結する
```scala
val list1 = List("aaa", "bbb")
val list2 = List("ccc", "ddd")
val list3 = list1 ::: list2
// list3 = List("aaa", "bbb", "ccc", "ddd") になる
```

既存のリストの先頭に新しい要素を追加する
```scala
val list4 = List("yyy", "zzz")
val list5 = "xxx" :: list4
// list5 = List("xxx", "yyy", "zzz") になる
```

こんな初期化もできる
```scala
val list6 = 1 :: 2 :: 3 :: Nil
```

### リストへの要素の追加（append）について  
Listクラスは要素の末尾への追加（append）をサポートしているが、追加要素に要する時間がリストサイズに比例するためあまり使用されない。  
リストの末尾に要素を次々追加していきたい場合は、先頭に挿入してから最後にreverseを呼び出す。  
もしくは追加操作要素を持つミュータブルなリストのListBufferを使う。  


<a name="3.3"></a>
## 3.3 [ステップ9] タプルを使う
タプルはリストと同様にイミュータブルだが、異なる型の要素を持つことができる。  
（整数と文字列の両方を同時に格納できる）  

```scala
val pair = (99, "hoge")

// 1つめの要素へのアクセス
pair._1
// 2つめの要素へのアクセス
pair._2
```

* タプルの実際の型は、格納している要素の数とそれらの要素の型によって決まる。  
    * (99, "hoge") の型は Tuple2[Int, String]  
    * ('a', 'b', "hoge", 1, 2, "fuga") の型は Tuple6[char, char, String, Int, Int, String]  
* 要素へのアクセスの際、_Nの数値が0からではなく1から始まっているのは、HaskalやMLなどの他の言語によって、静的に型付けされたタプルは1から始まるという伝統が築かれているからである。  
* タプルの要素は22個までしか作れない。  


<a name="3.4"></a>
## 3.4 [ステップ10] 集合とマップを使う
* 集合（set）とマップ（map）についても、Scalaはミュータブル版とイミュータブル版を持っている。  
* デフォルトではイミュータブルで、ミュータブルを使用する時は「import scala.collection.mutable.Set」と宣言する必要がある。  

イミュータブル集合
```scala
// イミュータブルな集合に再代入する必要がある場合、valではなくvarにしなければならない
var imuSet = Set("hoge", "fuga")

// 実質的には imuSet = imuSet + "zzz"
imuSet += "zzz"
println(imuSet.contains("aaa"))
```

ミュータブル集合
```scala
import scala.collection.mutable.Set
val muSet = Set("hoge", "fuga")
muSet += "zzz"
println(muSet)
```

イミュータブルマップ
```scala
val imuMap = Map( 1 -> "hoge", 2 -> "fuga", 3 -> "zzz")
println(imuMap)
```

ミュータブルマップ
```scala
import scala.collection.mutable.Map
val muMap = Map[Int, String]()
muMap += (1 -> "hoge")
muMap += (2 -> "fuga")
muMap += (3 -> "zzz")
println(muMap(2))
```





<a name="matome"></a>
## まとめ
* applyというファクトリーメソッド・・・？デザインパターン・・・おっふ・・・
* applyメソッドはArrayコンパニオンオブジェクトで定義されている  
→コンパニオンオブジェクト・・・クラス名と同じ名前のオブジェクト
