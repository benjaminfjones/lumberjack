package lumberjack;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lumberjack.Hello;

public class TestHello {

        @Test
        public void testHello() {
                Hello.hello();
                assertEquals("for large values of 2", 2 ,2);
        }

        @Test
        public void testReturnTrue() {
                assertTrue("Yes, it's true", Hello.returnTrue());
        }

}
