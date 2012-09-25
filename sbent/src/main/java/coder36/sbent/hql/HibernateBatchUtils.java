/*
 * Copyright 2012 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package coder36.sbent.hql;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

/**
 * When using hibernate for batch processing, steps should be taken to minimize the nubmer
 * of SQL calls.   HibernateBatchUtils addresses this concern.
 *
 * Typical usage pattern:
 *
 *  Step 1:
 *  ItemReader configured to return list of id's:
 *
 *  <bean id="itemReader" class="org.springframework.batch.item.database.JdbcCursorItemReader">
 *	  <property name="dataSource" ref="dataSource"/>
 *	  <property name="sql" value="select c.id, b.id from Customer c, Bank b where b.cus_id=cus.id"/>
 *	  <property name="rowMapper" ref="longRowMapper"/>
 *	 </bean
 *
 *  Num SQL calls = 1
 *
 *  (I wouldn't bother using HibernateItemReader as you can not be sure it will fan into multiple SQL calls w
 *  which could be very costly)
 *
 *  Step 2:
 *  The ItemWriter.write() method  will be presented with an array of Longs:
 *  ids = { {1,5}, {2,5}, {3,5}, {4,6} } - convert these into hibernate Customer and Bank entites :
 *
 *  Class [] classes = { Customer.class, Bank.class };
 *  Object ojs [] = HibernateUtils.read( session, ids, classes );
 *
 *  Num SQL calls = 2
 *
 *  for ( Object o[] : objs ) {
 *    Customer c = (Customer) o[0];
 *    Bank b = (Bank) o[1]
 *  }
 *
 *  By using this pattern, the number of SQL calls can be reduced to the bare minimum.
 *
 *
 * @author Mark Middleton
 */
public class HibernateBatchUtils {

	/**
	 * ids is am Array of:
	 * {LongA1,LongA2..}
	 * {LongB1,LongB2..}
	 * {LongC1,LongC2..}
	 * ...
	 * This methods pulls a column into an array list whilst removing any duplicates:
	 * {LongA1,LongB1,LongC1...)
	 * @param ids
	 * @param index
	 * @return Collection of longs
	 */
	private static Collection<Long> read( List<? extends Long[]> ids, int index ) {
		HashSet<Long> h = new HashSet<Long>();
		for( Long [] a: ids ) {
			h.add(  a[index] );
		}
		return h;
	}

	/**
	 * Reads a hibernate result set into the current session, more specifically result sets of the form Object [] - ie. hibernate queries
	 * which return multiple id columns, joined together by a primary column (in the example u.id)  eg:
	 * select u.id, p.id, a.id from AccountInstruction u, Provider p, Account a where a.provider=p ...
	 *
	 * @param hibernateTemplate
	 * @param ids
	 * @param class types
	 * @return List of re-read hibernate object
	 */
	public static List<Object []> read( Session session, List<? extends Long[]> ids, Class [] types ) {

		List<Object[]> l = new ArrayList<Object[]>();

		// read objects into hibernate cache.  This should result in a select statement per entity type
		for( int i=0; i < types.length; i++ ) {
			Criteria criteria = session.createCriteria(types[i]);
			criteria.add(Expression.in("id", read(ids,i)));
			criteria.list();
		}
		// from this point onwards, all the entities should be available locally
		// calls to session.get(...) should be free!

		// combine the cached objects into an object array { type[0], type[1] .. }
		for ( Long[] x: ids ) {
			Object [] array = new Object[types.length];
			for( int i=0; i<types.length; i++ ) {
				if ( x[i] == null ) {
					array[i] = null;
				}
				else {
					array[i] = session.get( types[i], x[i] );
				}
			}
			l.add(array);
		}
		return l;
	}
}


