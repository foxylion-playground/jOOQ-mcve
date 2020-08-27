/*
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.mcve.test;

import static org.jooq.mcve.Tables.TEST;
import static org.junit.Assert.assertEquals;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;

public class MCVETest {

    private final DSLContext ctx = DSL.using(SQLDialect.POSTGRES);

    private final Query query = ctx.select(TEST.ID)
            .from(TEST)
            .where(TEST.VALUE.isFalse())
            .and(TEST.ID.eq(42));

    @Test
    public void brokenWithExtractParams() {
        long namedParametersInSql = ctx.renderNamedParams(query).chars().filter(ch -> ch == ':').count();
        long namedParametersByExtractParams = ctx.extractParams(query).size();

        assertEquals(namedParametersInSql, namedParametersByExtractParams);
    }

    @Test
    public void worksWithBindValues() {
        long namedParametersInSql = ctx.render(query).chars().filter(ch -> ch == '?').count();
        long namedParametersByExtractBindValues = ctx.extractBindValues(query).size();

        assertEquals(namedParametersInSql, namedParametersByExtractBindValues);
    }
}
