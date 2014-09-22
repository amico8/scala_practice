def approximate(guess: Double): Double =
  if (isGoodEnough(guess)) guess
  else approximate(improve(guess))

def approximateLoop(initialGuess:Double): Double = {
  var guess = initialGuess
  while (!isGoodEnough(guess))
    guess = improve(guess)
  guess
}

def boom(x: Int): Int =
  if (x == 0) throw new Exception("boom!") else boom(x - 1)

def isEven(x: Int): Boolean =
  if (x == 0) true else isOdd(x - 1)
def isOdd(x: Int): Boolean =
  if (x == 0) false else isEven(x - 1)

val funValue = nesredFun _
def nesredFun(x: Int) {
  if (x != 0) { println(x); funValue(x - 1) }
}
