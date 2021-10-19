
import com.raywenderlich.android.omgandroid.Bar
import com.raywenderlich.android.omgandroid.Book
import com.raywenderlich.android.omgandroid.Foo
import junit.framework.Assert
import org.junit.Test

import org.junit.Assert.*


class MainActivityTest {

    private val book = Book(null, "1995", null, null)

    @Test
    fun testfoo() {
        val bar = object : Bar {
            override fun test(): Int {
                return 10
            }
        }

        val foo = Foo(bar)
        assertEquals(20, foo.add())
    }
}
