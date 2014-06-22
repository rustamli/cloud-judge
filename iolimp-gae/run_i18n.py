__author__ = 'turan@rustam.li'

import yaml
from mako.template import Template

def run(lang, page):
    f = open("lang/" + lang + "/data/" + page + ".yaml")
    data = yaml.safe_load(f)
    f.close()

    f = open("tpl/" + page + ".tpl.html")
    template_src = f.read()
    f.close()

    f = open("lang/" + lang + "/tpl/" + page + ".html", "w")
    f.write(Template(template_src, default_filters=["decode.utf8"], input_encoding='utf-8',
                     output_encoding='utf-8',).render(**data))
    f.close()
    pass


def build_all():
    pages = ["index"]

    for page in pages:
        run("en", page)
        run("az", page)
        run("ru", page)


build_all()