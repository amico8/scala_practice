# 第7章 組み込みの制御構造
* [7.1 if式](#7.1)
* [7.2 whileループ](#7.2)
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

関数型に書き換えてみる
```scala
var say = if (!args.isEmpty) args(0) else "hogehoge"
```
* valを使用しているので、変数が書き換えられてないかをチェックする手間が省ける
* 等式推論をサポートしやすくなる
* 式に副作用がなければ、valによって導入される変数は、それを計算する式と等しい

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
<console>:12: warning: comparing values of types Unit and Unit using ’==’ will always yield true
              greet() == ()
                      ^
warning: there was one deprecation warning; re-run with -deprecation for details
hi
res8: Boolean = true
```









<a name="matome"></a>
## まとめ
