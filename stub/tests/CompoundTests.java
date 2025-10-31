
/*
 *   Copyright (C) 2022 -- 2025  Zachary A. Kissel
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import org.junit.Test;

public class CompoundTests extends LangTest
{
    @Test
    public void equalNotEqual()
    {
        runTypeTest("equalNotEqual", "(5 != 3) or (2 = 2);", "bool");
        runEvalTest("equalNotEqual", "(5 != 3) or (2 = 2);", "true");
    }

    @Test
    public void falseAndExpr()
    {
        runTypeTest("falseAndExpr", "2 > 3 and 2 = 2;", "bool");
        runEvalTest("falseAndExpr", "2 > 3 and 2 = 2;", "false");
    }

    @Test
    public void trueAndExpr()
    {
        runTypeTest("trueAndExpr", "2 < 3 and 2 = 2;", "bool");
        runEvalTest("trueAndExpr", "2 < 3 and 2 = 2;", "true");
    }

    @Test
    public void falseOrExpr()
    {
        runTypeTest("falseOrExpr", "2 > 3 or 2 = 2;", "bool");
        runEvalTest("falseOrExpr", "2 > 3 or 2 = 2;", "true");
    }

    @Test
    public void trueOrExpr()
    {
        runTypeTest("trueOrExpr", "2 < 3 or 6 > 4;", "bool");
        runEvalTest("trueOrExpr", "2 < 3 or 6 > 4;", "true");
    }

    @Test
    public void lessAndMore()
    {
        runTypeTest("lessAndMore", "(5 < 7) and (7 > 3);", "bool");
        runEvalTest("lessAndMore", "(5 < 7) and (7 > 3);", "true");
    }

    @Test
    public void listLenLess()
    {
        runTypeTest("listLenLess", "len([5, 7]) < 4;", "bool");
        runEvalTest("listLenLess", "len([5, 7]) < 4;", "true");
    }

    @Test
    public void multiStatement()
    {
        runTypeTest("multiStatement",
                "val x := 3 + 5 * 2;\nval y := 4 + x;\nx > y;", "bool");
        runEvalTest("multiStatement",
                "val x := 3 + 5 * 2;\nval y := 4 + x;\nx > y;", "false");
    }
}
