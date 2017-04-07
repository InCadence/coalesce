/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.incadencecorp.coalesce.services.search.service.expert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/hello")
public class CommonController {

    @RequestMapping(method = RequestMethod.GET)
    public String printHello(ModelMap model)
    {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        Map<String, Object> columns = new LinkedHashMap<String, Object>();
        columns.put("Entity Key", "A");
        columns.put("Title", "B");

        rows.add(columns);

        columns = new LinkedHashMap<String, Object>();
        columns.put("Entity Key", "C");
        columns.put("Title", "D");

        rows.add(columns);

        model.addAttribute("rows", rows);
        return "hello";
    }

}
