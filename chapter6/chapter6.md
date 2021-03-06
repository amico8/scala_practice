# 第6章 関数型スタイルのオブジェクト
* [6.1 Rationalクラスの仕様](#6.1)
* [6.2 Rationalの構築](#6.2)
* [6.3 toStringメソッドのオーバーライド](#6.3)
* [6.4 事前条件のチェック](#6.4)
* [6.5 フィールドの追加](#6.5)
* [6.6 自己参照](#6.6)
* [6.7 補助コンストラクター](#6.7)
* [6.8 非公開フィールドとメソッド](#6.8)
* [6.9 演算子の定義](#6.9)
* [6.10 Scalaの識別子](#6.10)
* [6.11メソッドの多重定義](#6.11)
* [6.12 暗黙の型変換](#6.12)
* [6.13 [使用上の注意]演算子メソッドと暗黙の型変換](#6.13)
* [まとめ](#matome)

---

<a name="6.1"></a>
## 6.1 Rationalクラスの仕様
* 有理数（分数）の加減乗除を実行するオブジェクトを作る
* 分数はミュータブルな状態が無いため、イミュータブルなオブジェクトにする  
（分数と別の分数を加算することはできるが、その結果は新しい分数である）
* 個々の分数は、1個のRationalオブジェクトによって表現される
* 2個のRationalオブジェクトを加算するときは、その和を保持する新しいRationalオブジェクトを作る

```scala
// こんなかんじのものをつくりたい
scala > val oneHalf = new Rational(1, 2)
oneHalf: Rational = 1 / 2

scala > val twoThirds = new Rational(2 / 3)
twoThirds: Rational = 2 / 3

scala > (oneHalf / 7) + (1 - twoThirds)
res0: Rational = 17 / 42
```

### イミュータブルの長所
* 時間とともに変化する複雑な状態空間を持たないので、ミュータブルなオブジェクトよりも動作を推定しやすい
* コピーを作らずに他のオブジェクトに渡すことができる
* 複数のスレッドが同時にアクセスしても、状態を壊す恐れがない
* ハッシュテーブルのキーを安全に作れる

### イミュータブルの短所
* 大規模なオブジェクトグラフのコピーが必要になる場合がある  
＝ 部分変更ができないので、変更する必要がある場合は丸々コピーする必要があり、性能の面でボトルネックとなる可能性がある


<a name="6.2"></a>
## 6.2 Rationalの構築
* Rationalはイミュータブルなオブジェクトにするため、クライアントはインスタンス構築時に分子と分母を提供しなければならない
* コンパイル時、2つのクラスパラメータを集めて、同じ2個のパラメータを取る基本コンストラクタを生成する
```scala
// nが分子でdが分母
class Rational(n: Int, d: Int)
```
* Javaは、クラスがコンストラクタを持ち、コンストラクタがパラメータを受け取る
* Scalaは、クラスが直接パラメータを受け取る

```scala
// Rationalクラスの作成
scala> class Rational(n:Int, d:Int) {
     |   // インスタンス時に実行される
     |   println("Created " + n + " / " +d)
     | }
defined class Rational

// インスタンスの生成
scala> new Rational(1, 2)
Created 1 / 2
res0: Rational = Rational@90110a
```


<a name="6.3"></a>
## 6.3 toStringメソッドのオーバーライド
* 上記でインスタンス生成時に出力された「res0: Rational = Rational@90110a」は、RationalオブジェクトのtoStringメソッドが呼び出している
* 標準ではjava.lang.Objectで定義されたtoStringが呼び出され、「クラス名@16進数」の形式になる。らしい
* しかしこれではあまり役に立たないので、Rationalの分母、分子を出力するように、toStringメソッドをオーバーライドする

```Scala
scala> class Rational(n: Int, d: Int) {
     |   // toStringメソッドをオーバーライド
     |   override def toString = n + " / " + d
     | }
defined class Rational

// インスタンスの生成
scala> val x = new Rational(1, 3)
x: Rational = 1 / 3
```

* toStringメソッドは、デバッグ出力文やログメッセージ、テストのエラーレポート、インタプリタやデバッガの出力として使用する事が多い


<a name="6.4"></a>
## 6.4 事前条件のチェック
* 今のままだと、分母に0がきても受け付けてしまう。分母に0が渡された時は、Rationalが作られないよう事前条件として定義すべき
* requireメソッドを使用する
```Scala
scala> class Rational(n: Int, d: Int) {
     |   // false(分母が0)ならIllegalArgumentExceptionを投げる
     |   require(d != 0)
     |   override def toString = n + " / " + d
     | }
defined class Rational
```


<a name="6.5"></a>
## 6.5 フィールドの追加
* 分数の足し算
```
n1/d1 + n2/d2 = (n1 * d2) + (n2 * d1) / (d1 * d2)
↓
分子：(n1 * d2) + (n2 * d1)
分母：(d1 * d2)
```

* 加算処理のメソッドを定義してみる
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  override def toString = n + " / " + d
  // 加算のメソッド（クラスとして定義すると結果型として使える）
  def add(that: Rational): Rational =
    new Rational(n * that.d + that.n * d, d * that.d)
}

// 実行するとエラーになる（that.dとthat.nにはアクセスできない）
<console>:13: error: value d is not a member of Rational
           new Rational(n * that.d + that.n * d, d * that.d)
                                 ^
<console>:13: error: value n is not a member of Rational
           new Rational(n * that.d + that.n * d, d * that.d)
                                          ^
<console>:13: error: value d is not a member of Rational
           new Rational(n * that.d + that.n * d, d * that.d)
                                                          ^
```

* クラスパラメータのnやdは、Addメソッドのスコープ内にはあるが、addはレシーバーのdやnにしかアクセスできない
* コンストラクタ引数にvalやvarを定義しないと、外部からのアクセスはできない

外部からアクセスできるよう、フィールドを追加する
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  // フィールド追加
  val numer: Int = n
  val denom: Int = d
  override def toString = numer + " / " + denom
  def add(that: Rational): Rational =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )
}
```

オブジェクトの外から、フィールドにアクセスすることができるようになった
```scala
scala> val r = new Rational(1, 2)
r: Rational = 1 / 2

scala> r.numer
res12: Int = 1

scala> r.denom
res13: Int = 2
```

加算もできるようになった
```scala
scala> val r1 = new Rational(1, 3)
r1: Rational = 1 / 3

scala> val r2 = new Rational(2, 5)
r2: Rational = 2 / 5

scala> r1 add r2
res14: Rational = 11 / 15
```


<a name="6.6"></a>
## 6.6 自己参照
* numer, denomは単独で書いているが、呼び出し元の変数だと明示するためにも「this」キーワードをつけた方が好ましい

```scala
// レシーバがパラメータのRationalよりも小さいかどうかを判定するメソッド
def lessThan(that: Rational) = 
  this.numer * that.denom < that.numer * this.denom  // 2つの分数の大小関係は、分母を揃えた時の分子で比較する


// 2つのRationalオブジェクトを生成
scala> val r1 = new Rational(1, 3)
r1: Rational = 1 / 3
scala> val r2 = new Rational(1, 4)
r2: Rational = 1 / 4

// r1の方が大きいのでfalse
scala> r1 lessThan r2
res15: Boolean = false
```

```scala
// レシーバとパラメータで、大きい方を返すメソッド
def max(that: Rational) =
  if (this.lessThan(that)) that else this


// 2つのRationalオブジェクトを生成
scala> val a = new Rational(1,2)
a: Rational = 1 / 2
scala> val b = new Rational(1,3)
b: Rational = 1 / 3

// aの方が大きいのでaを返す
scala> a max b
res19: Rational = 1 / 2
```

レシーバとパラメータについて
```
// hogeがレシーバ、hugaがパラメータ
hoge.メソッド名(huga)
hoge 演算子 huga
```


<a name="6.7"></a>
## 6.7 補助コンストラクター
Scalaでは、基本コンストラクタ以外のコンストラクタを「補助コンストラクタ」と呼ぶ
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  val numer: Int = n
  val denom: Int = d
  // 補助コンストラクタ追加
  def this(n: Int) = this(n, 1)
  override def toString = numer + " / " + denom
  def add(that: Rational): Rational =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )
}
```
* Scalaの補助コンストラクタは、先頭を「def this(...)」とする
* すべての補助コンストラクタは、同じクラスの他のコンストラクタを呼びださなければならない  
（すべてのScalaクラスに含まれる補助コンストラクタの最初の方では、this(...)と記述する）
* この規則には、すべてのコンストラクタ呼び出しが最終的にクラスの基本コンストラクタ呼び出しに落ち着くという効果がある
* なので、別の補助コンストラクタを呼び出してから別処理をしないと怒られる


<a name="6.8"></a>
## 6.8 非公開フィールドとメソッド
private宣言を使って、約分処理を追加する
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  // 最大公約数を求める
  private val g = gcd(n.abs, d.abs)
  // 最大公約数で割った値を分子・分母とする
  val numer = n / g
  val denom = d / g
  def this(n: Int) = this(n, 1)
  def add(that: Rational): Rational =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )
  override def toString = numer + " / " + denom
  // 最大公約数を計算するメソッド（ユーリックドの互助算で再帰呼び出し）
  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}
```


<a name="6.9"></a>
## 6.9 演算子の定義
加算のメソッド名が「add」で使いにくいため、「+」という名前のメソッドで定義する（ついでに蒸散のメソッドも作る）
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  private val g = gcd(n.abs, d.abs)
  val numer = n / g
  val denom = d / g
  def this(n: Int) = this(n, 1)
  // メソッド名を「+」に変更
  def + (that: Rational): Rational =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )
  // 乗算のメソッド
  def * (that: Rational): Rational =
    new Rational(numer * that.numer, denom * that.denom)
  override def toString = numer + " / " + denom
  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}
```
```scala
// 2つのRationalオブジェクトを生成
scala> val a = new Rational(1, 2)
a: Rational = 1 / 2
scala> val b = new Rational(2, 5)
b: Rational = 2 / 5

// 加算
scala> a + b
res0: Rational = 9 / 10

// 乗算
scala> a * b
res1: Rational = 1 / 5

// *メソッドは+メソッドよりも優先される
scala> a + a * b
res2: Rational = 7 / 10

// ()も使える
scala> (a + a) * b
res3: Rational = 2 / 5
```


<a name="6.10"></a>
## 6.10 Scalaの識別子
識別子・・・変数や関数を区別するための名前

### 英数字識別子
* 先頭が英字か「\_」で、その後に英字、数字、「\_」を続ける
* 「$」も英字の1つだが、これはScalaコンパイラが生成する識別子で使うため使用してはいけない
* 「\_」もScalaでは識別子以外の目的でよく使われるため、使用は避けたほうがよい
* 先頭を大文字にするキャメルケース
    * クラス
    * トレイト
    * 定数  
    （Javaでは定数名には大文字を使い、単語の間に「\_」を入れる習慣になっている。Scalaは最初の文字を大文字にする）
* 先頭を小文字にするキャメルケース
    * フィールド
    * メソッドのパラメータ
    * ローカル変数
    * 関数の名前

### 演算子識別子
* 1個以上の演算子文字から構成される  
（演算子文字・・・「+、:、?、^、#などの印字可能なASCII文字）
* 対話コンソールで識別子を定義すると出力されるように、Scalaコンパイラは演算子識別子を内部的にすり潰して使っている
* すり潰しは、例えば"<-"が"$less$minus"みたいに置き換えられるこ
* JavaコードからこれらのScalaコードにアクセスする場合は"<-"ではアクセス出来ないので"$less$minus"といった内部表現形式を使う必要がある
* x <- y というScalaコードは、演算識別子をつなげて使う文化がないらしいJavaでは x < - yで字句解析されている

### ミックス識別子
* 英数字識別子に「\_」と演算子識別子が続く形
* 「unary_+」・・・単項演算子「+」を定義するメソッドの名前
* 「myvar_=」・・・代入演算子を定義するメソッドの名前

### リテラル識別子
* バッククォート「｀」で囲まれた任意の文字列
* Scala予約語を識別子としたい時に使う
* JavaのThreadクラスの静的メソッド「yierd」へのアクセス・・・「Thread.｀yierd｀()」と書く


<a name="6.11"></a>
### 6.11メソッドの多重定義
現状だと、整数をミックスした計算（分数に整数をかける等）はできないので、分数と整数の間で加減乗除できるメソッドを追加する
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  private val g = gcd(n.abs, d.abs)
  val numer = n / g
  val denom = d / g
  def this(n: Int) = this(n, 1)
  // 加算
  def + (that: Rational): Rational =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )
  def + (i: Int): Rational =
    new Rational(numer + i * denom, denom)

  // 減算
  def - (that: Rational): Rational =
    new Rational(
      numer * that.denom - that.numer * denom,
      denom * that.denom
      )
  def - (i: Int): Rational =
    new Rational(numer - i * denom, denom)

  // 乗算
  def * (that: Rational): Rational =
    new Rational(numer * that.numer, denom * that.denom)
  def * (i: Int): Rational =
    new Rational(numer * i, denom)

  // 除算
  def / (that: Rational): Rational =
    new Rational(numer * that.denom, denom * that.numer)
  def / (i: Int): Rational =
    new Rational(numer, denom * i)

  override def toString = numer + " / " + denom
  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}
```
* 新しいクラスでは、個々の算術演算メソッドが2種類ずつ実装されている  
個々のメソッド名が複数のメソッドで使われているので、これらのメソッドは**多重定義**されている

多重定義したメソッドは、非演算子の型によって自動に振り分けられる
```scala
scala> val x = new Rational(2, 3)
x: Rational = 2 / 3

scala> x * x
res0: Rational = 4 / 9

scala> x * 2
res1: Rational = 4 / 3
```


<a name="6.12"></a>
## 6.12 暗黙の型変換
* 「x * 2」のように、「2 * x」も書けるようにしたいが、現状ではエラーになる
* RationalクラスはScala標準のライブラリクラスではないため、Int型は「2.*(x)」というようなRational型のパラメータを受け取ることができない
* この解決法として、必要な時に自動的に整数を分数に変換する**暗黙の型変換**を定義できる

```scala
// IntからRationalへの変換メソッドを定義
scala> implicit def intToRational(x: Int) = new Rational(x)
warning: there was one feature warning; re-run with -feature for details
intToRational: (x: Int)Rational

scala> var r = new Rational(2, 3)
r: Rational = 2 / 3

scala> 2 * r
res2: Rational = 4 / 3
```
* implicit修飾子は、コンパイラーに対し様々な状況で自動的に適用せよと支持する
* 暗黙の型変換を機能させるには、そのメソッドがインタプリタのスコープ内になければならない


<a name="6.13"></a>
## 6.13 [使用上の注意]演算子メソッドと暗黙の型変換
* ライブラリを設計する時に目標として忘れてはならないことは、単にクライアントコードを簡潔にするだけでなく、読みやすく理解しやすいものにすること
* 簡潔さは読みやすさの大きな構成要素だが、簡潔すぎてわからないということもある


<a name="matome"></a>
## まとめ
* なかなか理解が遅くなってきた・・・書いてあることを理解はできるけど、自分で思いつくのはむずかしいなぁ
* ユーリクッドの互助算って・・・ムムム・・・；
* 識別子むずかしい・・・
