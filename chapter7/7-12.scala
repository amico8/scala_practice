import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

try {
  val f = new FileReader("input.txt")
} catch {
  case ex: FileNotFoundException => println("File not found.")
  case ex: IOException => ("IO Err.")
} finally {
  //f.close()
}
