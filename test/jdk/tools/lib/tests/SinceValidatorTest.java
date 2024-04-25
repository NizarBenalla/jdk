/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @summary Verify SinceValidator works properly
 * @library /test/langtools/tools/lib
 * @modules jdk.compiler/com.sun.tools.javac.api
 *          jdk.compiler/com.sun.tools.javac.code
 *          jdk.compiler/com.sun.tools.javac.main
 *          jdk.compiler/com.sun.tools.javac.util
 * @build toolbox.ToolBox toolbox.JavacTask SinceValidator
 * @run main SinceValidatorTest
*/

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import toolbox.TestRunner;
import toolbox.JavacTask;
import toolbox.JavaTask;
import toolbox.Task;
import toolbox.ToolBox;

public class SinceValidatorTest extends TestRunner {

    ToolBox tb;

    public static void main(String... args) throws Exception {
        new SinceValidatorTest().runTests();
    }

    SinceValidatorTest() {
        super(System.err);
        tb = new ToolBox();
    }

    public void runTests() throws Exception {
        runTests(m -> new Object[] { Paths.get(m.getName()) });
    }

    @Test
    public void testSimple(Path base) throws Exception {
        Path current = base.resolve(".");
        Path src = current.resolve("src");
        Path classes = current.resolve("classes");
        buildVersion(src.resolve("9"),
                     classes.resolve("9"),
                     """
                     /**
                      * @since 9
                      */
                     module test {
                         exports api;
                     }
                     """,
                     """
                     package api;
                     /**
                      * @since 9
                      */
                     public class Api {}
                     """);
        buildVersion(src.resolve("10"),
                     classes.resolve("10"),
                     """
                     /**
                      * @since 9
                      */
                     module test {
                         exports api;
                     }
                     """,
                     """
                     package api;
                     /**
                      * @since 9
                      */
                     public class Api {
                         public void test() {}
                     }
                     """);
        List<String> actualErrors;
        actualErrors = SinceValidator.runTest(new SinceValidator.Configuration() {
            @Override
            public IntStream versionsToCheck() {
                return IntStream.of(9, 10);
            }

            @Override
            public String moduleName() {
                return "test";
            }

            @Override
            public List<String> optionsForAnalyzeVersion(int version) {
                return List.of("--add-modules", "test", "--module-path", classes.resolve(String.valueOf(version)).toString());
            }

            @Override
            public List<String> optionsForVerify() {
                return List.of("--module-path", classes.resolve("10").toString());
            }

            @Override
            public List<Path> moduleSourcePath() {
                return List.of(src.resolve("10"));
            }
        });
        List<String> expectedErrors = List.of(
            "For Element: method:api.Api:test:() Wrong @since version is 9 instead of 10"
        );
        if (!Objects.equals(expectedErrors, actualErrors)) {
            throw new AssertionError("Expected errors not found, expected: " + expectedErrors +
                                     ", got: " + actualErrors);
        }
    }

    private void buildVersion(Path src, Path classes, String... sources) throws Exception {
        Path moduleSources = src.resolve("test");
        tb.writeJavaFiles(moduleSources, sources);

        Files.createDirectories(classes);

        new JavacTask(tb)
            .outdir(classes)
            .files(tb.findJavaFiles(moduleSources))
            .run(Task.Expect.SUCCESS)
            .writeAll();
    }
}
