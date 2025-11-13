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
package ast.typesystem.types;

/**
 * Represents a list type with an element type.
 * 
 * @author Zach Kissel
 */
public final class ListType extends Type
{
    private Type elementType; // The type of elements in the list

    /**
     * Construct a new list type with the given element type.
     * 
     * @param elementType the type of the list elements
     */
    public ListType(Type elementType)
    {
        this.elementType = elementType;
    }

    /**
     * Get the type of the list elements.
     * 
     * @return the element type
     */
    public Type getElementType()
    {
        return elementType;
    }

    /**
     * Check equality of list types. Two list types are equal if they have
     * equivalent element types.
     * 
     * @param obj the object to test
     * @return true if obj is a ListType with the same element type
     */
    @Override
    public boolean equals(Object obj)
    {
        // Check to see if we are comparing to ourself.
        if (obj == this)
            return true;

        // Make sure we are looking at a list type.
        if (!(obj instanceof ListType))
            return false;

        ListType rhs = (ListType) obj;

        // Check if element types are equal
        return this.elementType.equals(rhs.elementType);
    }

    /**
     * Gets the type as a string in the externalized form.
     * The format is the element type enclosed in brackets.
     * 
     * @return the type as a string
     */
    @Override
    public String toString()
    {
        return "[ " + elementType.toString() + " ]";
    }
}
