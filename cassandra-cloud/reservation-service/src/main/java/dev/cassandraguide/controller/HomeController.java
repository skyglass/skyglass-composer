/*
 * Copyright (C) 2017-2020 Jeff Carpenter
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
 */
package dev.cassandraguide.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redirect to the API documentation page
 *
 * @author Cedrick Lunven
 */
@RestController
@RequestMapping("/")
public class HomeController {
    
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
    public String redirectToDoc() {
        return new StringBuilder(""
                + "<html>"
                + " <head>"
                + "  <meta http-equiv=\"refresh\" content=\"0; url=swagger-ui.html\" />"
                + " </head>"
                + " <body/></html>").toString();
    }

}
