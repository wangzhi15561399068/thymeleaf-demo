/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.anyun.common.lang.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author TwitchGG <ray@proxzone.com>
 * @since 1.0.0 on 8/26/16
 */
public interface ContextBuilder {

    ContextBuilder withOptions(String options);

    Context build() throws NamingException;
}
