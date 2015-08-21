/* 
* This file is part of DISMOD Core. 
* © Copyright 2003-2011 Fraunhofer Institut f. Materialfluss und Logistik.
* http://www.iml.fraunhofer.de, http://www.verkehrslogistik.de
*
* DISMOD Core is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* DISMOD Core is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with DISMOD Core.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.fhg.iml.vlog.ination;

/**
 * @author Stefan Tannenbaum, Bernd Schmidt
 */
public interface LocaleChangeListener {
	/** Called, after the locale of the corresponding iNation has been changed. */
	public abstract void localeChanged();
}
