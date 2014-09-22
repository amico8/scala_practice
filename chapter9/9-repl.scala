// [9-2]
def containsNeg(nums: List[Int]): Boolean = {
  var exists = false
  for (num <- nums)
    if (num < 0)
      exists = true
    exists
}

def containsNeg(nums: List[Int]) = nums.exists(_ < 0)

////////
def containsOdd(nums: List[Int]): Boolean = {
  var exists = false
  for (num <- nums)
    if (num % 2 == 1)
      exists = true
    exists
}

def containsOdd(nums: List[Int]) = nums.exists(_ % 2 == 1)

// [9-3]
def first(x: Int) = (y: Int) => x + y
var second = first(1)
second(2)

val onePlus = curriedSum(1)_
onePlus(2)
val twoPlus = curriedSum(2)_
  twoPlus(2)

// [9-4]
def twice(op: Double => Double, x: Double) = op(op(x))
twice(_ + 1, 5)

////////
import java.io.File
import java.io.PrintWriter
def withPrintWriter(file: File, op: PrintWriter => Unit) {
  val writer = new PrintWriter(file)
  try {
    op(writer)
  } finally {
    writer.close()
  }
}

withPrintWriter(
  new File("date.txt"),
  writer => writer.println(new java.util.Date)
  )

//[9-5]
var assertionsEnabled = true
def myAssert(predicate: () => Boolean) =
  if (assertionsEnabled && !predicate())
    throw new AssertionError
