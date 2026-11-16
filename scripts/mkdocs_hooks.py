"""MkDocs hook: rewrite links that leave docs/ to absolute GitHub URLs.

The docs are written to work when browsed on GitHub, so they link to files such
as ../AGENTS.md or ../src/... . Those files are not part of the docs site, so on
GitHub Pages the same links point to the blob/tree view on GitHub instead.
"""

import posixpath
import re

REPO_URL = "https://github.com/olx-india/java-service-template-clean-architecture"
BRANCH = "main"

# [text](target) or [text](target "title"), skipping images' alt text is fine.
_LINK = re.compile(r"\]\((?P<target>[^)\s]+)(?P<rest>[^)]*)\)")


def _rewrite(target: str, src_uri: str) -> str | None:
    if re.match(r"^[a-z][a-z0-9+.-]*:", target) or target.startswith(("#", "/")):
        return None
    path, sep, anchor = target.partition("#")
    page_dir = posixpath.dirname(src_uri)
    resolved = posixpath.normpath(posixpath.join("docs", page_dir, path))
    if resolved.startswith("docs/") or resolved == "docs":
        return None
    kind = "tree" if path.endswith("/") else "blob"
    return f"{REPO_URL}/{kind}/{BRANCH}/{resolved.rstrip('/')}" + (sep + anchor if sep else "")


def on_page_markdown(markdown, page, config, files):
    src_uri = page.file.src_uri

    def repl(match):
        new = _rewrite(match.group("target"), src_uri)
        if new is None:
            return match.group(0)
        return f"]({new}{match.group('rest')})"

    return _LINK.sub(repl, markdown)
