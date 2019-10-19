/*
 * (C) Copyright 2019 The PrematicLibraries Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 29.06.19 10:52
 *
 * The PrematicLibraries Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.prematic.libraries.utility;

/**
 * This small convert library helps you to convert any object in a specified primitive type.
 */
public class Convert {

    /**
     * Convert a input object to a string vale.
     *
     * @param input The object to convert
     * @return The input object as string
     */
    public static String toString(Object input){
        return input.toString();
    }

    /**
     * Convert a input object to a character vale.
     *
     * <p>A convert problem will return 0</p>
     *
     * @param input The object to convert
     * @return The input object as character
     */
    public static char toCharacter(Object input){
        if(input instanceof Character) return (char) input;
        else if(input instanceof Number) return (char) input;
        else{
            String content = input.toString();
            if(content.length() > 0) return content.charAt(0);
            else return 0;
        }
    }

    /**
     * Convert a input object to a boolean vale.
     *
     * <p>A convert problem will return false</p>
     *
     * @param input The object to convert
     * @return The input object as boolean
     */
    public static boolean toBoolean(Object input){
        if(input instanceof Boolean) return (boolean) input;
        else if(input instanceof Integer) return (Integer) input == 1;
        else if(input instanceof Long) return (Long) input == 1;
        else if(input instanceof Double) return (Double) input == 1;
        else if(input instanceof Float) return (Float) input == 1;
        else if(input instanceof Byte) return (Byte) input == 1;
        else if(input instanceof Number) return ((Number) input).intValue() == 1;
        else return input.toString().equalsIgnoreCase("true");
    }

    /**
     * Convert a input object to a integer vale.
     *
     * <p>A convert problem will return 0</p>
     *
     * @param input The object to convert
     * @return The input object as integer
     */
    public static int toInteger(Object input){
        if(input instanceof Number) return ((Number) input).intValue();
        else if(input instanceof Character) return (int)input;
        else if(input instanceof Boolean) return ((Boolean)input?1:0);
        else{
            try{
                return Integer.parseInt(input.toString());
            }catch (NumberFormatException ignored){
                return 0;
            }
        }
    }

    /**
     * Convert a input object to a long vale.
     *
     * <p>A convert problem will return 0</p>
     *
     * @param input The object to convert
     * @return The input object as long
     */
    public static long toLong(Object input){
        if(input instanceof Number) return ((Number) input).longValue();
        else if(input instanceof Character) return (int)input;
        else if(input instanceof Boolean) return ((Boolean)input?1:0);
        else{
            try{
                return Long.parseLong(input.toString());
            }catch (NumberFormatException ignored){
                return 0;
            }
        }
    }

    /**
     * Convert a input object to a double vale.
     *
     * <p>A convert problem will return 0</p>
     *
     * @param input The object to convert
     * @return The input object as double
     */
    public static double toDouble(Object input){
        if(input instanceof Number) return ((Number) input).doubleValue();
        else if(input instanceof Character) return (int)input;
        else if(input instanceof Boolean) return ((Boolean)input?1:0);
        else{
            try{
                return Double.parseDouble(input.toString());
            }catch (NumberFormatException ignored){
                return 0;
            }
        }
    }

    /**
     * Convert a input object to a float vale.
     *
     * <p>A convert problem will return 0</p>
     *
     * @param input The object to convert
     * @return The input object as float
     */
    public static float toFloat(Object input){
        if(input instanceof Number) return ((Number) input).floatValue();
        else if(input instanceof Character) return (int)input;
        else if(input instanceof Boolean) return ((Boolean)input?1:0);
        else{
            try{
                return Float.parseFloat(input.toString());
            }catch (NumberFormatException ignored){
                return 0;
            }
        }
    }

    /**
     * Convert a input object to a short vale.
     *
     * <p>A convert problem will return 0</p>
     *
     * @param input The object to convert
     * @return The input object as short
     */
    public static short toShort(Object input){
        if(input instanceof Number) return ((Number) input).shortValue();
        else if(input instanceof Character) return (byte)input;
        else if(input instanceof Boolean) return (short) ((Boolean)input?1:0);
        else{
            try{
                return Short.parseShort(input.toString());
            }catch (NumberFormatException ignored){
                return 0;
            }
        }
    }

    /**
     * Convert a input object to a byte vale.
     *
     * <p>A convert problem will return 0</p>
     *
     * @param input The object to convert
     * @return The input object as byte
     */
    public static byte toByte(Object input){
        if(input instanceof Number) return ((Number) input).byteValue();
        else if(input instanceof Character) return (byte)input;
        else if(input instanceof Boolean) return (byte) ((Boolean)input?1:0);
        else{
            try{
                return Byte.parseByte(input.toString());
            }catch (NumberFormatException ignored){
                return 0;
            }
        }
    }

}
