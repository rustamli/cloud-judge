
import os

import jinja2
import webapp2
from webapp2_extras import sessions


JINJA_ENVIRONMENT = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)),
    extensions=["jinja2.ext.autoescape"],
    autoescape=True)


class MainPage(webapp2.RequestHandler):
    def dispatch(self):
        self.session_store = sessions.get_store(request=self.request)

        try:
            webapp2.RequestHandler.dispatch(self)
        finally:
            self.session_store.save_sessions(self.response)

    @webapp2.cached_property
    def session(self):
        return self.session_store.get_session()

    def get(self):
        self.response.headers["Content-Type"] = "text/html"

        switch_to_lang = self.request.get("lang")
        if switch_to_lang:
            self.session["lang"] = switch_to_lang
            self.redirect(self.request.path)

        lang = self.session.get("lang") or "az"

        template_values = {
            'lang': lang
        }

        template = JINJA_ENVIRONMENT.get_template("lang/" + lang + "/tpl/index.html")
        self.response.write(template.render(template_values))


config = {'webapp2_extras.sessions': {
    'secret_key': 'berlin8996',
}}

application = webapp2.WSGIApplication([
    ('/', MainPage),
], config=config, debug=True)