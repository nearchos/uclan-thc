/*
 * This file is part of UCLan-THC server.
 *
 *     UCLan-THC server is free software: you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     UCLan-THC server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with GetCategories.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.uclan.thc.api.json;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.model.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Retrieves the list of available categories.
 *
 * User: Nearchos Paspallis
 * Date: 17/12/13
 * Time: 21:30
 */
public class GetCategories extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String code = request.getParameter("code");
        final boolean usingCode = code != null && code.length() > 0;

        final Vector<Category> categories = usingCode ?
                CategoryFactory.getCategoriesByCode(code) :
                CategoryFactory.getAllCategories();
        if(categories.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Empty", "No categories found"));
        }
        else
        {
            final StringBuilder reply = new StringBuilder("{").append(EOL);

            final Vector<String> selectedCategories = new Vector<String>();
            for(final Category category : categories)
            {
//                if(usingCode || category.isActiveNow()) // if using code, then show also inactive, otherwise show only active
//                {
                    selectedCategories.add("{ \"uuid\": \"" + category.getUUID() + "\", \"name\": \"" + category.getName() + "\", \"locationUUID\": \"" + category.getLocationUUID() + "\", \"validFrom\": \"" + category.getValidFromAsString() + "\", \"validUntil\": \"" + category.getValidUntilAsString() + "\" }");
//                }
            }

            reply.append("  \"status\": \"OK\"").append(",").append(EOL); // OK status
            reply.append("  \"categories\": [").append(EOL); // OK status

            int counter = 0;
            for(final String selectedCategory : selectedCategories)
            {
                reply.append("    ").append(selectedCategory).append(counter++ < selectedCategories.size() - 1 ? "," : "").append(EOL);
            }
            reply.append("  ]").append(EOL);
            reply.append("}").append(EOL);

            printWriter.println(reply.toString()); // normal JSON output
        }
    }
}