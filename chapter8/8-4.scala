def printTime2(out: java.io.PrintStream = Console.out,
               divisor: Int = 1) =
  out.println("time = " + System.currentTimeMillis()/divisor)

//println(printTime2(out = Console.err))
//println(printTime2(divisor = 1000))
