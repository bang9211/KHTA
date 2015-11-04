/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
 *
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
package infra.type;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public enum RoadType {
    HIGHWAY("Highway",101),
    FREEWAY("Freeway",102),
    ROUTE("Route",103),
    SCROAD("Special City Road",104),
    S_STATEROAD("State Road supported by National",105),
    STATEROAD("State Road",106),
    CITYROAD("City Road",107),
    B_PRIVATEHIGHWAY("Bailed Private Highway",108),
    PRIVATEHIGHWAY("Private Highway",109),
    ETC("etc",110);
    
    private RoadType(String _desc, int _code){
        desc = _desc;
        code = _code;
    }
    
    public final String desc;
    public final int code;
}
