﻿using System;
using System.Collections.Generic;

namespace AdminClient.Code.Utils
{
    internal static class CollectionUtils
    {
        public static void ForEach<TIn>(this IEnumerable<TIn> collection, Action<TIn> action)
        {
            foreach (var item in collection)
                action(item);
        }

        public static void ForEach<TIn, TOut>(this IEnumerable<TIn> collection, Func<TIn, TOut> action)
        {
            foreach (var item in collection)
                action(item);
        }
    }
}