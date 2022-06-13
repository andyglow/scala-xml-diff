/**
 * scala-xml-diff
 * Copyright (c) 2014, Andrey Onistchuk, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 *
 * ------
 *
 * This code is derived from cats. The cats license follows:
 *
 * Cats Copyright (c) 2015 Cats Contributors.

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.andyglow.xml.diff

import scala.util.control.TailCalls._

private[diff] sealed abstract class Eval[+A] extends Serializable { self =>
  def value: A

  def map[B](f: A => B): Eval[B] =
    flatMap(a => Eval.Now(f(a)))

  def flatMap[B](f: A => Eval[B]): Eval[B] =
    this match {
      case Eval.Now(_) =>
        Eval.FlatMap[B, A](
          start = () => self,
          run = f
        )

      case c @ Eval.FlatMap(_, _) => c.wrap(f)
    }
}

private[diff] object Eval {

  final case class Now[A](value: A) extends Eval[A]

  final case class FlatMap[A, Start](
    start: () => Eval[Start],
    run: Start => Eval[A]
  ) extends Eval[A] { self =>
    def value: A = evaluate(this)
    def wrap[B](f: A => Eval[B]): FlatMap[B, Start] =
      Eval.FlatMap[B, Start](
        start = self.start,
        run = (s: Start) =>
          Eval.FlatMap[B, A](
            start = () => self.run(s),
            run = f
          )
      )
  }

  sealed abstract private class FnStack[A, B]
  final private case class Ident[A, B](ev: A <:< B) extends FnStack[A, B]
  final private case class Many[A, B, C](first: A => Eval[B], rest: FnStack[B, C]) extends FnStack[A, C]

  private def evaluate[A](e: Eval[A]): A = {
    def loop[A1](curr: Eval[A1], fs: FnStack[A1, A]): TailRec[A] =
      curr match {
        case c @ FlatMap(_, _) =>
          c.start() match {
            case cc @ FlatMap(_, _) =>
              tailcall(loop(cc.start(), Many(cc.run, Many(c.run, fs))))

            case Now(value) =>
              tailcall(loop(c.run(value), fs))
          }

        case Now(value) =>
          fs match {
            case Many(f, fs) => tailcall(loop(f(value), fs))
            case Ident(ev)   => done(ev(value))
          }
      }

    loop(e, Ident(implicitly[A <:< A])).result
  }
}
