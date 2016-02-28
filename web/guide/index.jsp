<%--
  ~ This file is part of UCLan-THC server.
  ~
  ~     UCLan-THC server is free software: you can redistribute it and/or
  ~     modify it under the terms of the GNU General Public License as
  ~     published by the Free Software Foundation, either version 3 of
  ~     the License, or (at your option) any later version.
  ~
  ~     UCLan-THC server is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%--
  User: Nearchos Paspallis
  Date: 4/10/13
  Time: 09:48
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <link rel="stylesheet" type="text/css" href="../uclan_thc.css">
        <title>API Guide</title>
    </head>

    <body>

    <div style="background-image: url(../background_stripes.png); height: 158px; margin-bottom:0;">
        <div style="width:800px;margin:0 auto;padding-top:70px;padding-bottom:10px;">
            <div style="margin-left: 10px; float: left;">
                <a href="http://uclan-thc.appspot.com">
                    <img alt="University of Central Lancashire (UCLan)"
                         align="left"
                         src="../uclan.png"/></a>
                <h1>Treasure Hunt Challenge</h1>
            </div>
        </div>
    </div>

        <div style="width:800px;margin:0 auto;padding-top:20px;padding-bottom:10px;padding-right:10px;a link: #99cc00">

            <h2>API Guide</h2>

            <p>This guide provides a quick tutorial on how the Treasure Hunt Challenge API can be used.</p>

            <h3>Overview of how the Treasure Hunt Challenge Web Service works</h3>

            <p>To begin a quiz, you need to make a request to the server, telling it which category of questions you
                would like to use for the quiz (i.e. you need to specify the UUID of the requested category).</p>
            <p>The server will then select the corresponding set of questions from the database, and assign that list
                of questions to a new quiz session. It will then send you a Session UUID, which your app needs to
                remember, and quote in all further communications with the server when playing that specific quiz
                session.</p>
                <blockquote>If you send a request to the server, but do not use one of the specified API procedure
                    calls, you are likely to receive an error message from App Engine (the server software). This is
                    a different sort of error message to the kind you might legitimately get from the web service
                    itself when something is wrong with the values of the parameters you have sent in an otherwise
                    correctly structured message.</blockquote>
            <p>The session ID can also be used to recover a game part-way through should your App terminate
                unexpectedly (that’s posh for Crash).</p>
            <p>The server will only give you the questions one at a time, and you must have answered one correctly
                (or skipped it) before it will give you the next one. The server will not send any of the answers to
                your app – you have to send an answer to the server and it will “mark” it for you.</p>
            <p>From the list of questions it has prepared for you, the server will keep track of which question in the
                list you are up to, and what your score is (according to the session UUID).</p>
            <p>At any time, after you started a new session, you may ask for the current question. The server will give
                you the question and the list of possible answers. All the questions are text-based.</p>
                <blockquote>Some questions offer multiple possible answers (i.e. multiple choice questions) which you
                    handle simply by submitting the correct answer’s number (e.g. A, B, etc.)</blockquote>
            <p>You can attempt to answer the current question at any time by sending your answer to the server. You may
                also use the “skip” call to move directly on to the next question.</p>
            <p>When you submit a correct answer or skip, the server will move on to the next question, and update your
                score appropriately (+10 for a correct answer, -5 for skipping a question). You may ask the server for
                your current score at any time.</p>
            <p>After the quiz has ended, you may also ask the server for a table of all submitted scores.</p>

            <h3>Quick overview of API calls</h3>

            <hr/>
            <p><b>/api/csv/categories</b></p>
            <p>
                Retrieves the list of available categories. Can be called at any time without a player name or session ID. Retrieves a list of categories.
                <br/>
                <a href="/guide/categories.html" class="uclan_btn" align="right">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/startQuiz?playerName=...&appID=...&categoryUUID=...</b></p>
            <p>
                Initiates a new quiz session for the specified category. The session is automatically created if no session is already available for the given parameters combination.
                <br/>
                <a href="/guide/startQuiz.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/currentQuestion?session=...</b></p>
            <p>
                Retrieves the current question for the specified session.
                The returned data include the ID of the question, the text of the question, and whether the question requires a lat/lng pair to be answered.
                <br/>
                <a href="/guide/currentQuestion.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/answerQuestion?answer=...&session=...</b></p>
            <p>
                Submits a proposed answer for the current question. A correct answer is rewarded with +10 points.
                <br/>
                <a href="/guide/answerQuestion.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/updateLocation?session=...&lat=...&lng=...</b></p>
            <p>
                Updates the current position for the specified session. No data is returned.
                <br/>
                <a href="/guide/updateLocation.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/skipQuestion?session=...</b></p>
            <p>
                Skips the current question in the current session. This action costs -5 points.
                <br/>
                <a href="/guide/skipQuestion.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/score?session=...</b></p>
            <p>
                Retrieves the current score for the specified session (i.e. the same user/app).
                <br/>
                <a href="/guide/score.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/scoreBoard?session=...</b></p>
            <p>
                Retrieves the current score for all the players for the category of the specified session.
                <br/>
                <a href="/guide/scoreBoard.html" class="uclan_btn">View details</a>
            </p>

            <hr/>

            <h3>Quiz App Design</h3>

            <p>Typically, a quiz will follow the process of calling <a href="/guide/categories.html">categories</a>, using the
                returned data to allow the player to choose a category, and then use the selection as parameter to call
                <a href="/guide/startQuiz.html">startQuiz</a>. There will then follow a repeating cycle of calling
                <a href="/guide/currentQuestion.html">currentQuestion</a> followed by
                <a href="/guide/answerQuestion.html">answerQuestion</a> (possibly more than once per cycle, as
                the player gets the answers wrong). Your App will also be expected to submit location updates using the
                <a href="/guide/updateLocation.html">updateLocation</a> API call, and optionally provide the option to
                <a href="/guide/skipQuestion.html">skipQuestion</a>. Typically, your App will also
                call <a href="/guide/score.html">score</a> whenever a question is answered or skipped. The cycle is repeated
                until the number of questions is exhausted. After this, <a href="/guide/scoreBoard.html">scoreboard</a> may be
                called.</p>
            <p>To facilitate the above, a simple interface might have a “setup” screen (i.e. Arrangement), a “question and
                answer” screen and an “end” screen, which will be hidden and revealed as appropriate.</p>

        </div>

    </body>

</html>