Bond - the spy agent
====================

Bond spies on your users and tells you how many times a day each user made a
request on your site.  It records who the user is, what url they request and
when the request was made.

Bond is made up of two parts: Django middleware and a Clojure server that
parses all the collected data and shows you a nice graph. Bond uses Redis to
store data and Rickshaw to display the graphs.

Use case
--------

If you are a freelancer, you need your clients to go and make sure the app
you're building for them does what they want.  You build the thing and then you
send them an email, requesting that they check it out and give you feedback.  I
find that this sometimes takes forever.  Bond allows you to see if the client
has viewed your site and what parts of it they inspected.  I find this
especially helpful in volunteer situations where the client isn't financially
involved.  With Bond, you don't have to keep asking, *Have you checked out the
changes I made?*

django middleware
-----------------

Add the middleware to your `MIDDLEWARE_CLASSES`:

    MIDDLEWARE_CLASSES = (
        'yourproject.middleware.BondMiddleware',
        'django.middleware.common.CommonMiddleware',
        'django.contrib.sessions.middleware.SessionMiddleware',
        'django.middleware.csrf.CsrfViewMiddleware',
        'django.contrib.auth.middleware.AuthenticationMiddleware',
        'django.contrib.messages.middleware.MessageMiddleware',
    )

You can add an excludes entry to your `settings.py`:

    BOND_EXCLUDES = (
        '/admin/'
    )

Note that `startswith` is used to match the urls.

Clojure server
--------------

This is a simple Noir app.

Edit `bond.config` and:

    cd server
    lein deps
    lein run

Open your browser to `http://localhost:8080`.

Disclaimer
----------

This is my first Clojure project and I'm still very much learning the language
and the idioms.

License
-------

BSD, short and sweet.
