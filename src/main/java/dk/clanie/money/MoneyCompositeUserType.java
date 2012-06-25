/**
 * This file is part of the Money library.
 * Copyright (C) 2007, 2012, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package dk.clanie.money;

import static org.hibernate.type.StandardBasicTypes.BIG_DECIMAL;
import static org.hibernate.type.StandardBasicTypes.CURRENCY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

public class MoneyCompositeUserType implements CompositeUserType {

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Class returnedClass() {
		return Money.class;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value, SessionImplementor session)
			throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y)
			return true;
		if (x == null || y == null)
			return false;
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		BigDecimal amount = rs.getBigDecimal(names[0]);
		if (rs.wasNull())
			return null;
		String currencyCode = rs.getString(names[1]);
		Currency currency = Currency.getInstance(currencyCode);
		return new Money(amount, currency);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, BIG_DECIMAL.sqlType());
			st.setNull(index + 1, CURRENCY.sqlType());
		} else {
			Money money = (Money) value;
			st.setBigDecimal(index, money.getAmount());
			st.setString(index + 1, money.getCurrencyCode());
		}
	}

	@Override
	public String[] getPropertyNames() {
		return new String[] { "amount", "currency" };
	}

	@Override
	public Type[] getPropertyTypes() {
		return new Type[] { BIG_DECIMAL, CURRENCY };
	}

	@Override
	public Object getPropertyValue(Object component, int property)
			throws HibernateException {
		Money money = (Money) component;
		if (property == 0)
			return money.getAmount();
		else
			return money.getCurrencyCode();
	}

	@Override
	public void setPropertyValue(Object component, int property, Object value)
			throws HibernateException {
		throw new UnsupportedOperationException("Immutable Money");
	}

}
