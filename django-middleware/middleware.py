"""
Middleware for Bond
===================

(c) 2012 - Honza Pokorny - All rights reserved
Licensed under the BSD license
"""
import logging
from datetime import datetime
import redis
from django.conf import settings


logger = logging.getLogger(__name__)


class BondMiddleware(object):

    def process_response(self, request, response):
        if not hasattr(request, 'user'):
            return response

        if request.user.is_anonymous():
            return response

        try:
            path = request.path

            excludes = getattr(settings, 'BOND_EXCLUDES', None)

            if excludes:
                if path.startswith(excludes):
                    return response

            now = datetime.utcnow()
            date = now.strftime('%Y%m%d')
            now = now.strftime('%Y-%m-%d %H:%M:%S')

            r = redis.StrictRedis(host='localhost', port=6379, db=0)

            counter = r.incr('hits')
            message = {
                'user': request.user.username,
                'added': date,
                'datetime': now,
                'url': path
            }
            r.hmset('hit:%d' % counter, message)
        except:
            logger.warn("Couldn't register hit at %s" % path)

        return response
