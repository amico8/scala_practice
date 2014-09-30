# 第4章 クラスとオブジェクト
* [4.1 クラス、フィールド、メソッド](#4.1)
* [4.2 セミコロン推論](#4.2)
* [4.3 シングルトンオブジェクト](#4.3)
* [4.4 Scalaアプリケーション](#4.4)
* [4.5 Appトレイト](#4.5)
* [まとめ](#matome)

---

<a name="4.1"></a>
## 4.1 クラス、フィールド、メソッド
* 特になし


<a name="4.2"></a>
## 4.2 セミコロン推論
* 演算子は行頭ではなく行末に揃える


<a name="4.3"></a>
## 4.3 シングルトンオブジェクト
### 今更ながらstatic
* クラスに属するのがstatic（インスタンスありなし関係なく使える）、インスタンスに属するのが非static
* staticなメソッドから、非staticなメンバにアクセスすることはできない
* staticなメソッド内でthisは使えない
* 呼び出し方：クラス名.変数名　クラス名.メソッド名(引数)

### Scalaに戻って・・・
* Scalaは、クラスがstaticを持てない  
その代わりシングルトンオブジェクトを持っている
* シングルトンオブジェクトの例がこちら↓
```scala
// コンパニオンクラス
class ChecksumAccumulator {
  private var sum = 0
  def add(b: Byte) {sum += b}
  def checksum():Int = ~(sum & 0xff) + 1
}

// コンパニオンオブジェクト
import scala.collection.mutable.Map
object ChecksumAccumulator {
  private val cache = Map[String, Int]()
  def calculate(s: String): Int =
    if (cache.contains(s))
      cache(s)
    else {
      val acc = new ChecksumAccumulator
      for (c <- s)
        acc.add(c.toByte)
      val cs = acc.checksum()
      cache += (s -> cs)
      cs
    }
}
```

* シングルトンオブジェクトがクラス名と同じ名前＝そのクラスの**コンパニオンオブジェクト**という
* クラスとコンパニオンオブジェクトは、同じソースファイルで定義しなければならない
* クラスとコンパニオンオブジェクトは、互いの非公開メンバにアクセスできる
* コンパニオンクラスをもたないオブジェクトは、スタンドアロンオブジェクトと呼ぶ
* クラスはパラメータをとれるが、シングルトンオブジェクトはパラメータをとれない

```scala
// 実行するとき
ChecksumAccumulator.calculate("Every value is an object.")
```

* シングルトンオブジェクトは、スーパークラスを継承しトレイトをミックスインできる  
（クラスとトレイトを継承するシングルトンオブジェクトの例は、第13章で）
* シングルトンオブジェクトは、何らかのコードから初めてアクセスされた時に初期化される


<a name="4.4"></a>
## 4.4 Scalaアプリケーション
* Scalaプログラムを実行するには、mainメソッドを持つスタンドアロンシングルトンオブジェクトの名前を指定する
```scala
import ChecksumAccumulator.calculate

object Summer {
  def main(args :Array[String]) {
    for (arg <- args)
      println(arg + ": " + calculate(arg))
  }
}
```

* Scalaのファイル名は、Javaのようにクラス名と合わせなくてもエラーにはならないが、一般的には合わせた方がプログラマが検索しやすい

### Scalaのコンパイル
```shell
$ scalac ChecksumAccumulator.scala Summer.scala
```

ただし、このコンパイラは起動するたびにjarファイルの内容をスキャンするなどの初期作業に時間がかかるため、fscというコンパイラデーモンもある。

```shell
$ fsc ChecksumAccumulator.scala Summer.scala
```


<a name="4.5"></a>
## 4.5 Appトレイト
* Scalaは、scala.App というトレイトを提供して、コード入力を少しでも減らせるようにしている

Appトレイトの使い方
```scala
import ChecksumAccumulator.calculate

object FallWinterSpringSummer extends App {
  // このコードは、クラスが初期化される時に実行される
  for (season <- List("fall", "winter", "spring"))
    println(season + ": " + calculate(season))
}
```
* シングルトンオブジェクトの名前の後に**extends App**と書く
* mainメソッドを書く代わりに、mainメソッドに入れるはずだったコードを中括弧の間に書く
* 継承されたmainをScalaアプリケーションとして使う

### Appトレイトの欠点
* args配列にアクセスできないので、コマンド行引数にアクセスしなければならないときは使えない
* Java仮想マシンのスレッドモデルが持つ制限から、マルチスレッドプログラムは明示的なmainメソッドを必要とする
* Java仮想マシンの一部の実装は、Appトレイトが実行するオブジェクトの初期化コードを最適化しない

** Appトレイトを継承するのは、プログラムが比較的単純でシングルスレッドの時だけに限る **


<a name="matome"></a>
## まとめ
* スーパークラスを継承してトレイトをミックスイン・・・？トレイトって？ミックスインって？？
* デザインパターンのシングルトンがちゃんと理解できてない・・・
* アプリケーションのエントリーポイント？

